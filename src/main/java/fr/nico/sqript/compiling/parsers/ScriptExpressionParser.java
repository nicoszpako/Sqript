package fr.nico.sqript.compiling.parsers;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.expressions.ExprCompiledExpression;
import fr.nico.sqript.expressions.ExprPrimitive;
import fr.nico.sqript.expressions.ExprReference;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.ExpressionDefinition;
import fr.nico.sqript.meta.MatchResult;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.TransformedPattern;
import fr.nico.sqript.types.primitive.TypeString;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptExpressionParser implements INodeParser {


    @Override
    public Node parse(ScriptToken line, ScriptCompilationContext compilationContext, Class[] type) throws ScriptException {
        return parse(line, compilationContext, null, type);
    }

    public Node parse(ScriptToken line, ScriptCompilationContext compilationContext, ScriptExpression parent, Class[] validTypes) throws ScriptException {
        //System.out.println();
        //System.out.println("Parsing : " + line + " wanting " + Arrays.toString(validTypes) + " from parent " + parent);

        String expressionString = line.getText();

        /*
         * We check if this line can be parsed by a simple parser.
         */
        for (INodeParser parser : ScriptDecoder.parsers) {
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

        /*
         * We loop through all registered expressions.
         */
        List<Node> validTrees = new ArrayList<>();
        for (ExpressionDefinition expressionDefinition : ScriptManager.expressions) {

            MatchResult[] matchResults = expressionDefinition.getMatchResults(expressionString);
            /*
             * If the expression matched, then we parse each found arguments, and we check if its type is convenient. If not, we check the next expression.
             */
            if (matchResults != null) {
                //System.out.println("Matched "+expressionDefinition.getExpressionClass()+":"+ Arrays.toString(matchResults));
                matchLoop:
                for (MatchResult matchResult : matchResults) {
                    if (expressionDefinition.getFeatures()[matchResult.getMatchedIndex()].side().isEffectivelyValid()) {

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

                        /*
                        Handling greedy expressions as ExprItems
                         */
                        if (expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].isGreedy()) {
                            //System.out.println("Checking for greedy : "+expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()]+" returning "+ expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType()+" " + "valid types are : "+Arrays.toString(validTypes));
                            if (!isTypeStrictlyValid(expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType(), validTypes))
                                continue;
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


                                List<String> parameters = new ArrayList<>();
                                /*
                                 * If an argument is a coma-separated string of sub-arguments, then we add them all as regular arguments to the expression.
                                 */
                                for (String argument : arguments) {
                                    if (argument != null)
                                        if (isComaSeparated(argument)) {
                                            //System.out.println("Is coma separated : " + argument);
                                            //System.out.println("Split is : " + Arrays.asList(ScriptDecoder.splitAtComa(argument)));
                                            parameters.addAll(Arrays.stream(ScriptDecoder.splitAtComa(argument)).map(ScriptDecoder::trim).collect(Collectors.toList()));
                                        } else {
                                            parameters.add(ScriptDecoder.trim(argument));
                                        }
                                    else parameters.add(null);
                                }

                                /*
                                 * We parse each sub-argument, and we gather them all in an ExprComplexEvaluation expression.
                                 */
                                Node[] subExpressions = new Node[parameters.size()];
                                int parameterIndex = 0;
                                for (String parameter : parameters) {
                                    if (parameter != null) {
                                        if (!parameter.isEmpty()) {
                                            if (!ScriptDecoder.isParenthesageGood(parameter))
                                                continue matchLoop;
                                            //System.out.println(debugOffset()+" Parsing subargument : "+parameter);
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
                                //System.out.println(debugOffset() + "Sub expressions are: " + Arrays.toString(subExpressions));

                            /*
                             * We check if the given types are verified by the expression instance.

                            for (int i = 0; i < subExpressions.length; i++) {
                                ScriptParameterDefinition[] associatedParameterDefinition = transformedPattern.getTypes()[i];
                                if (subExpressions[i] != null) {
                                    ScriptExpression subExpression = subExpressions[i].getExpression();
                                    if (subExpression != null && !checkTypes(associatedParameterDefinition, subExpression)) {
                                        //System.out.println("Not valid because incorrect types for " + expression);
                                        continue expressionLoop;
                                    }
                                }
                            }
                            */
                                nodeExpression.setChildren(subExpressions);
                                //System.out.println("Adding to switch : " + nodeExpression);
                                validTrees.add(nodeExpression);
                            } else {
                                //System.out.println(debugOffset() + "Not valid because not validated by expression.");
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        if (ScriptDecoder.checkIfVariable(line)) {
            //System.out.println("It's a variable : "+token);
            ScriptExpression expression = new ExprPrimitive(new TypeString(line.getText()));
            if (line.getText().contains("<"))
                expression = ScriptDecoder.compileVariableName(line, compilationContext);
            ExprReference reference = new ExprReference(expression);
            reference.setLine(line);
            Node nodeExpression = new NodeExpression(reference);
            validTrees.add(0, nodeExpression);
        }
        //System.out.println("Valid trees for " + line + " are " + validTrees);
        if (!validTrees.isEmpty()) {
            if (validTrees.size() == 1) {
                //System.out.println(debugOffset()+"Returning : "+validTrees.get(0));
                return validTrees.get(0);
            } else {
                if (validTypes.length == 1 && validTypes[0] == ScriptElement.class)
                    return validTrees.get(0);

                //System.out.println(debugOffset()+"Returning : "+result);
                return new NodeSwitch(validTrees.toArray(new Node[0]));
            }
        }

        /*
         * We check if the expression contains operators, if so we split at each operator, and we return an ExprAlgebraicEvaluation expression.
         * /!\ In this case, we place Nodes in a list as if they were in an infixed notation, and then we later transform this with infixToRPN, and then with rpnToAST.
         */
        if (ScriptDecoder.containsOperator(expressionString)) {
            //System.out.println("Parsing as algebraic expression : " + expressionString);
            /*
             * We parse the operators in the string.
             */
            ExtractedOperatorsResult operatorsBuiltResult = ScriptDecoder.buildOperators(expressionString);
            List<ScriptOperator> operators = Arrays.asList(operatorsBuiltResult.getOperators());
            String operatorsBuildString = operatorsBuiltResult.getTransformedString();
            operatorsBuildString = ScriptDecoder.trim(operatorsBuildString);

            /*
             * Now we extract each operand from the string, split at each operator.
             */
            ExpressionToken[] operatorSplitResult = ScriptDecoder.splitAtOperators(operatorsBuildString);
            List<ExpressionToken> tokens = Arrays.asList(operatorSplitResult);
            List<Node> nodes = new ArrayList<>();
            int addedOperators = 0;
            //System.out.println("Tokens are : "+tokens);
            if (tokens.size() > 1) {
                for (ExpressionToken token : tokens) {
                    if (token.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                        nodes.add(new NodeParenthesis(EnumTokenType.LEFT_PARENTHESIS));
                    } else if (token.getType() == EnumTokenType.RIGHT_PARENTHESIS) {
                        nodes.add(new NodeParenthesis(EnumTokenType.RIGHT_PARENTHESIS));
                    } else if (token.getType() == EnumTokenType.EXPRESSION) {
                        try {
                            Node node = parse(line.with(token.getExpressionString().trim()), compilationContext, new Class[]{ScriptElement.class});
                            if (node == null) {
                                return null;
                            }
                            nodes.add(node);
                        } catch (Exception ignored) {
                            return null;
                        }

                    } else if (token.getType() == EnumTokenType.OPERATOR) {
                        nodes.add(new NodeOperation(operators.get(addedOperators)));
                        addedOperators++;
                    }
                }
            }
            if (nodes.isEmpty())
                return null;
            else {
                try {
                    //System.out.println("Nodes are : "+nodes);
                    //System.out.println();
                    Node finalTree = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
                    //System.out.println("Final tree : "+finalTree);
                    if (validTypes != null && isTypeValid(finalTree.getReturnType(), validTypes)) {
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

    public static boolean isTypeValid(Class type, Class[] validTypes) {
        for (int i = 0; i < validTypes.length; i++) {
            //System.out.println("Checking if " + type + " is assignable from " + validTypes[i] + " : " + type.isAssignableFrom(validTypes[i]));
            if (validTypes[i] == ScriptElement.class || (validTypes[i] != null && type.isAssignableFrom(validTypes[i])))
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
