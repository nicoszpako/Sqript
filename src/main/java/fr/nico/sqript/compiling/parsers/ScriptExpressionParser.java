package fr.nico.sqript.compiling.parsers;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.expressions.*;
import fr.nico.sqript.meta.ExpressionDefinition;
import fr.nico.sqript.meta.MatchResult;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.TransformedPattern;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeFunction;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class ScriptExpressionParser implements INodeParser {

    public static List<INodeParser> parsers = new ArrayList<>();


    public static void init() {
        parsers.add((token, group, type) -> {
            //Check if it is a function
            ExprNativeFunction f;
            //Matcher m = ScriptDecoder.pattern_function.matcher(token.getText());
            //if (m.find()) {
            //System.out.println("Found for a function : "+m.group(1)+" with "+m.group(2)+" for "+token);
            //Priority to not native functions
            ScriptFunctionalBlock sf;
            NodeExpression functionNode = null;
            if ((sf = token.getScriptInstance().getFunction(token.getText().trim())) != null) {
                functionNode = new NodeExpression(new ExprFunction(sf));
                functionNode.setChildren(new Node[sf.parameters.length]);
            }
            if ((f = ScriptDecoder.parseNativeFunction(token)) != null) {
                functionNode = new NodeExpression(f);
                functionNode.setChildren(new Node[f.function.getNbParameters()]);
            }

            /*if (functionNode != null) {
                //System.out.println("Splitting "+m.group(2));
                for (String s : ScriptDecoder.splitAtComa(m.group(2))) {
                    //System.out.println("Adding function parameter : "+s);
                    functionNode.addChild(ScriptDecoder.parseExpressionTree(token.with(s), group, new Class[]{ScriptElement.class}));
                }
                return functionNode;
            }*/
            //}
            return functionNode;
        });

        parsers.add((line, group, type) -> {
            //Check for replaced string
            Matcher m = ScriptDecoder.pattern_capture_quotes.matcher(line.getText());
            if (m.matches()) {
                Matcher p = ScriptDecoder.pattern_percent.matcher(m.group(1));
                if (!p.find())
                    return new NodeExpression(new ExprPrimitive(new TypeString(m.group(2))));
                else {
                    return ScriptDecoder.compileString(line.with(m.group(2)), group);
                }
            }
            return null;
        });


        parsers.add((line, group, type) -> {
            //Check if it is a primitive
            PrimitiveType primitive = ScriptDecoder.parsePrimitive(line);
            //System.out.println("Primitive is null : "+(primitive==null));
            if (primitive != null) {
                //System.out.println("Found a primitive ! It's a : "+primitive.getClass());
                return new NodeExpression(new ExprPrimitive(primitive));
            }
            return null;
        });

        parsers.add((line, compilationContext, type) -> {
            //Check if it is a reference to an accessor or an other element that can be parsed
            //System.out.println("Checking accessors for line : "+line);
            ScriptTypeAccessor h = compilationContext.getAccessorFor(line.getText());
            if (h != null) {
                Class returnType = h.getReturnType();
                if (returnType == null)
                    throw new ScriptException.ScriptUndefinedReferenceException(line);
                else {
                    //System.out.println("Returning reference for : "+line);
                    ExprReference s = new ExprReference(h.getHash());
                    s.setLine((ScriptToken) line.clone());
                    s.setVarHash(h.getHash());
                    s.setReturnType(returnType);
                    //System.out.println("Hash is : "+h.getHash());
                    return new NodeExpression(s);
                }
            }
            return null;
        });


        parsers.add((line, group, type) -> {
            //Check if it is a not-native function reference
            //System.out.println("Checking if it's a function");
            if (line.getScriptInstance() != null)
                for (ScriptBlock fc : line.getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class)) {
                    //System.out.println("Checking for : "+fc.getClass().getSimpleName());
                    ScriptBlockFunction function = (ScriptBlockFunction) fc;
                    if (function.name.equals(line.getText())) {
                        //System.out.println("Returning function : "+function);
                        return new NodeExpression(new ExprResult(new TypeFunction(function)));
                    }
                }
            return null;
        });

        parsers.add((line, group, type) -> {
            //Check if it is an option
            if (line.getText().startsWith("@")) {
                if (line.getScriptInstance().getOptions().containsKey(line.getText().substring(1))) {
                    //System.out.println("Returning option for : "+line.getText());
                    return new NodeExpression(line.getScriptInstance().getOptions().get(line.getText().substring(1)));
                }
            }
            return null;
        });

        /*
         * Check if it is a variable
         */
        parsers.add((line, group, type) -> {
            if (ScriptDecoder.checkIfVariable(line)) {
                //System.out.println("It's a variable : "+line);
                ScriptExpression expression = new ExprPrimitive(new TypeString(line.getText()));
                if (line.getText().contains("<"))
                    expression = ScriptDecoder.compileVariableName(line, group);
                ExprReference reference = new ExprReference(expression);
                reference.setLine(line);
                //System.out.println("Returning : "+expression);
                return new NodeExpression(reference);
            }
            return null;
        });


    }

    @Override
    public Node parse(ScriptToken line, ScriptCompilationContext compilationContext, Class[] requiredTypes) throws ScriptException {
        return parse(line, compilationContext, null, requiredTypes);
    }

    public Node parse(ScriptToken line, ScriptCompilationContext compilationContext, ScriptExpression parent, Class[] requiredTypes) throws ScriptException {
        //System.out.println();

        //System.out.println("Parsing : " + line + " wanting " + Arrays.toString(requiredTypes) + " from parent " + parent);
        //System.out.println("Compilation context is : "+compilationContext);

        String expressionString = ScriptDecoder.trim(line.getText());
        String[] strings = ScriptDecoder.extractStrings(line.getText());
        String lineWithoutStrings = ScriptDecoder.removeStrings(line.getText(), strings);


        /*
         * We check if this line can be parsed by a simple parser.
         */
        Node node = externalParse(line, compilationContext, requiredTypes);
        if (node != null) return node;

        /*
         * We loop through all registered expressions.
         */
        List<Node> validTrees = parseValidTrees(line, compilationContext, parent, requiredTypes, expressionString, strings);


        //System.out.println("Valid trees for " + line + " are " + validTrees);
        /*
         * Cleaning valid trees
         */
        if (!validTrees.isEmpty()) {

            validTrees.removeIf(n -> {
                NodeExpression nodeExpression = (NodeExpression) n;
                /*
                 *   Handling greedy expressions like ExprItems
                 */
                ExpressionDefinition definition = ScriptManager.getDefinitionFromExpression(nodeExpression.getExpression().getClass());
                if (definition != null)
                    if (definition.transformedPatterns[nodeExpression.getExpression().getMatchedIndex()].isGreedy()) {
                        //System.out.println("Checking for greedy : " + definition.transformedPatterns[nodeExpression.getExpression().getMatchedIndex()].getPattern() + " returning " + definition.transformedPatterns[nodeExpression.getExpression().getMatchedIndex()].getReturnType() + " " + "valid types are : " + Arrays.toString(requiredTypes)+" for line "+line);
                        return !isTypeStrictlyValid(definition.transformedPatterns[nodeExpression.getExpression().getMatchedIndex()].getReturnType(), requiredTypes);
                    }
                return false;
            });
            //System.out.println("After filtered trees : "+validTrees);
            if (validTrees.size() == 1) {
                //System.out.println("Returning : "+validTrees.get(0));
                return validTrees.get(0);
            } else if (validTrees.size() > 1) {
                return new NodeSwitch(validTrees.toArray(new Node[0]));
            }
        }

        /*
         * We check if the expression contains operators, if so we split at each operator, and we return an ExprAlgebraicEvaluation expression.
         * /!\ In this case, we place Nodes in a list as if they were in an infixed notation, and then we later transform this with infixToRPN, and then with rpnToAST.
         */

        if (ScriptDecoder.isAlgebraic(lineWithoutStrings)) {
            //System.out.println("Parsing as algebraic expression : " + lineWithoutStrings);
            /*
             * We parse the operators in the string.
             */
            String operatorsBuildString = ScriptDecoder.buildOperators(line, expressionString);
            operatorsBuildString = ScriptDecoder.trim(operatorsBuildString);
            //System.out.println("Built string : "+operatorsBuildString);
            /*
             * Now we extract each operand from the string, split at each operator.
             */
            ExpressionToken[] operatorSplitResult = ScriptDecoder.splitAtOperators(operatorsBuildString);
            //System.out.println("Operator split result : "+Arrays.toString(operatorSplitResult));
            List<ExpressionToken> tokens = Arrays.asList(operatorSplitResult);
            //System.out.println("Tokens are : " + tokens);
            List<Node> nodes = new ArrayList<>();
            for (ExpressionToken token : tokens) {
                //System.out.println("Parsing elements from  " + line + " : " + token.getExpressionString());

                if (token.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                    nodes.add(new NodeParenthesis(EnumTokenType.LEFT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.RIGHT_PARENTHESIS) {
                    nodes.add(new NodeParenthesis(EnumTokenType.RIGHT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.EXPRESSION) {
                    if (token.getExpressionString().equals(expressionString)) {
                        return null;
                    }
                    try {
                        node = parse(line.with(token.getExpressionString().trim()), compilationContext, new Class[]{ScriptElement.class});
                        //System.out.println("Parsed node : " + node);
                        if (node == null) {
                            return null;
                        }
                        nodes.add(node);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                        return null;
                    }

                } else if (token.getType() == EnumTokenType.OPERATOR) {
                    nodes.add(new NodeOperation(token.getOperator()));
                }
            }
            if (nodes.isEmpty())
                return null;
            else {
                try {
                    //System.out.println("Nodes are : " + nodes);
                    //System.out.println();
                    Node finalTree = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
                    //System.out.println("Final tree for " + line + " : " + finalTree);
                    //System.out.println("Checking types for line : " + line + " as " + finalTree + " for " + Arrays.toString(requiredTypes));
                    //System.out.println("A");
                    //System.out.println(finalTree);
                    //System.out.println(finalTree.getReturnType());
                    //System.out.println("B");
                    if (finalTree != null && requiredTypes != null && isTypeValid(finalTree.getReturnType(), requiredTypes)) {
                        //System.out.println("Returning compiled : " + finalTree);
                        return finalTree;
                    }
                    //System.out.println("Type was not valid for : " + finalTree);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        //System.out.println("Returning null to " + line);
        return null;
    }

    private List<Node> parseValidTrees(ScriptToken line, ScriptCompilationContext compilationContext, ScriptExpression parent, Class[] validTypes, String expressionString, String[] replacedStrings) throws ScriptException {
        List<Node> validTrees = new ArrayList<>();
        for (ExpressionDefinition expressionDefinition : ScriptManager.expressions.values()) {
            //System.out.println("Testing "+line+" for "+expressionDefinition.getName());
            MatchResult[] matchResults = expressionDefinition.getMatchResults(expressionString);
            /*
             * If the expression matched, then we parse each found arguments, and we check if its type is convenient. If not, we check the next expression.
             */
            if (matchResults != null) {
                matchLoop:
                for (MatchResult matchResult : matchResults) {
                    if (expressionDefinition.getFeatures()[matchResult.getMatchedIndex()].side().isEffectivelyValid()) {
                        if (expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].isGreedy()) {
                            //System.out.println("Checking for greedy : " + expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getPattern() + " returning " + expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType() + " " + "valid types are : " + Arrays.toString(validTypes) + " for line " + line);
                            if (!isTypeStrictlyValid(expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType(), validTypes)) {
                                //System.out.println("Continuing as bad greedy");
                                continue;
                            }
                        }
                        //System.out.println("Matched " + expressionDefinition.getExpressionClass() + ":" + Arrays.toString(matchResults));

                        /*
                         * If we are parsing the exact same line that the parent on the exact same expression, then we continue to the next expression parsing check so that we don't enter an infinite loop.
                         */
                        if (parent != null) {
                            if (matchResult.getMatchedIndex() == parent.getMatchedIndex()
                                    && expressionDefinition.getExpressionClass() == parent.getClass()
                                    && line.equals(parent.getLine())) {
                                //System.out.println("Not valid because identical to parent for " + expressionString);

                            }
                            if (validTypes != null && !isTypeValid(expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType(), validTypes)) {
                                //System.out.println("Not valid because bad return type for " + expressionString + " as a " + Arrays.toString(validTypes) + " not assignable from " + expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType());
                                continue;
                            }

                        }


                        try {

                            ScriptExpression expression = expressionDefinition.instanciate((ScriptToken) line.clone(), matchResult.getMatchedIndex(), matchResult.getMarks());

                            TransformedPattern transformedPattern = expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()];

                            String[] arguments = transformedPattern.getAllArguments(expressionString);
                            //System.out.println("Next found expression is : " + expression.toString() + " with arguments " + Arrays.toString(arguments));

                            //System.out.println("Expression sub arguments are : " + Arrays.toString(arguments));

                            /*
                             * We run the method validate() to check if all is ok before returning the final result.
                             */
                            if (expression.validate(arguments, line)) {
                                NodeExpression nodeExpression = new NodeExpression(expression);
                                //System.out.println("Validated");

                                List<String> parameters = new ArrayList<>();
                                /*
                                 * If an argument is a coma-separated string of sub-arguments, then we add them all as regular arguments to the expression.
                                 */
                                //System.out.println("Arguments are: "+ Arrays.toString(arguments)+" ;"+arguments.length);
                                for (String argument : arguments) {
                                    if (argument != null) {
                                        //System.out.println("checking coma separated : "+argument);
                                        if (isComaSeparated(argument)) {
                                            //System.out.println("Is coma separated : " + argument);
                                            //System.out.println("Split is : " + Arrays.asList(ScriptDecoder.splitAtComa(argument)));
                                            parameters.addAll(Arrays.stream(ScriptDecoder.splitAtComa(argument)).map(a -> ScriptDecoder.replaceStrings(a, replacedStrings)).map(ScriptDecoder::trim).collect(Collectors.toList()));
                                        } else {
                                            parameters.add(ScriptDecoder.trim(argument));
                                        }
                                    } else {
                                        parameters.add(null);
                                    }
                                }

                                /*
                                 * We parse each sub-argument, and we gather them all in an ExprComplexEvaluation expression.
                                 */
                                Node[] subExpressions = new Node[parameters.size()];
                                //System.out.println("Parameters size is : "+parameters.size()+" : "+parameters);
                                int parameterIndex = 0;
                                for (String parameter : parameters) {
                                    if (parameter != null) {
                                        if (!parameter.isEmpty()) {
                                            if (!ScriptDecoder.isParenthesageGood(parameter))
                                                continue matchLoop;
                                            //System.out.println("Parsing subargument : " + parameter);
                                            int index = Math.min(parameterIndex, expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()]
                                                    .getTypes().length - 1);
                                            Class[] validParameterTypes = new Class[expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()]
                                                    .getTypes()[index].length];
                                            for (int i = 0; i < expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getTypes()[index].length; i++) {
                                                validParameterTypes[i] = expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getTypes()[index][i].getTypeClass();
                                            }
                                            //System.out.println("Sub parsing : " + line.with(parameter));
                                            subExpressions[parameterIndex] = parse(line.with(parameter), compilationContext, expression, validParameterTypes);
                                            //System.out.println("Parsed : " + subExpressions[parameterIndex]);
                                            if (subExpressions[parameterIndex] == null) {
                                                //System.out.println("Null non-optional sub-argument : skipping");
                                                continue matchLoop;
                                            }
                                        } else {
                                            subExpressions[parameterIndex] = null;
                                        }
                                    } else {
                                        subExpressions[parameterIndex] = null;
                                    }
                                    parameterIndex++;
                                }
                                //System.out.println("Sub expressions are: " + Arrays.toString(subExpressions));
                                nodeExpression.setChildren(subExpressions);
                                //System.out.println("Adding to valid list : " + nodeExpression);
                                validTrees.add(nodeExpression);
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return validTrees;
    }

    private Node externalParse(ScriptToken line, ScriptCompilationContext compilationContext, Class[] validTypes) throws ScriptException {
        for (INodeParser parser : parsers) {
            Node node;
            if ((node = parser.parse(line, compilationContext, validTypes)) != null) {
                //System.out.println("Parsed : " + node);
                if (!isTypeValid(node.getReturnType(), validTypes)) {
                    //System.out.println("Types are not valid : " + node.getReturnType() + " " + Arrays.toString(validTypes));
                    continue;
                }
                //System.out.println("Returning valid parturning : "+node);
                return node;
            }
        }
        return null;
    }

    public static boolean isTypeValid(Class type, Class[] validTypes) {
        //System.out.println("Is type valid : "+type+ " "+Arrays.toString(validTypes));

        // If type is null then it is uncertain, so it is considered as valid
        if (type == null)
            return true;
        for (Class validType : validTypes) {
            //System.out.println(type == null);
            if (validType == ScriptElement.class
                    || (validType != null && type != null && type.isAssignableFrom(validType))
                    || ScriptManager.isParsable(type, validType)
            )
                return true;
        }
        return false;
    }

    public static boolean isTypeStrictlyValid(Class type, Class[] validTypes) {
        for (int i = 0; i < validTypes.length; i++) {
            //System.out.println("Checking if " + type + " is assignable from " + validTypes[i] + " : " + type.isAssignableFrom(validTypes[i]));
            if (validTypes[i] != null && type.isAssignableFrom(validTypes[i]))
                return true;
        }
        return false;
    }


    public boolean isComaSeparated(String expressionString) {
        int c = 0;
        int ps = 0;
        while (c < expressionString.length()) {
            if (expressionString.charAt(c) == ')' || expressionString.charAt(c) == ']') {
                ps--;
            }
            if (expressionString.charAt(c) == '(' || expressionString.charAt(c) == '[') {
                ps++;
            }
            if (ps > 0) {
                c++;
                continue;
            }
            if (expressionString.charAt(c) == ',') {
                return true;
            }
            c++;
        }
        return false;
    }


}
