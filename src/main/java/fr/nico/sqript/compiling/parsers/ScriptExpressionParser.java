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
import fr.nico.sqript.structures.ScriptParameterDefinition;
import fr.nico.sqript.structures.TransformedPattern;
import fr.nico.sqript.types.primitive.TypeString;
import scala.actors.migration.pattern;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptExpressionParser implements IParser {


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
        for (IParser parser : ScriptDecoder.parsers) {
            Node node;
            if ((node = parser.parse(line, compilationContext, validTypes)) != null) {
                //System.out.println("Parsed : " + node);
                if (!isTypeValid(node.getReturnType(), validTypes)) {
                    //System.out.println("Types are not valid : " + node.getReturnType() + " " + Arrays.toString(validTypes));
                    continue;
                }
                //System.out.println("Returning valid parsed");
                //System.out.println(debugOffset() +"Returning : "+node);
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
                                //System.out.println(debugOffset() + "Not valid because identical to parent for " + expressionString);

                            }
                            if (validTypes != null && !isTypeValid(expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType(), validTypes)) {
                                //System.out.println(debugOffset() + "Not valid because bad return type for " + expressionString + " as a " + Arrays.toString(validTypes) + " not assignable from " + expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getReturnType());
                                continue;
                            }

                        }
                        try {

                            ScriptExpression expression = expressionDefinition.instanciate((ScriptToken) line.clone(), matchResult.getMatchedIndex(), matchResult.getMarks());

                            TransformedPattern transformedPattern = expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()];
                            //System.out.println("Next found expression is : " + expression.toString());

                            String[] arguments = transformedPattern.getAllArguments(expressionString);
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
                                            parameters.addAll(Arrays.asList(ScriptDecoder.splitAtComa(argument)));
                                        } else {
                                            parameters.add(argument);
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
                                            //System.out.println(debugOffset()+" Parsing subargument : "+parameter);
                                            Class[] validParameterTypes = new Class[expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getTypes()[parameterIndex].length];
                                            for (int i = 0; i < expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getTypes()[parameterIndex].length; i++) {
                                                validParameterTypes[i] = expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()].getTypes()[parameterIndex][i].getTypeClass();
                                            }
                                            subExpressions[parameterIndex] = parse(line.with(parameter), compilationContext, expression, validParameterTypes);
                                            if (subExpressions[parameterIndex] == null)
                                                continue matchLoop;
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
                                //System.out.println("Adding to switch : " + expression + " with " + Arrays.toString(subExpressions));
                                nodeExpression.setChildren(subExpressions);
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
            Node expression;
            if (line.getText().contains("%"))
                expression = ScriptDecoder.compileString(line, compilationContext);
            else {
                ExprReference reference = new ExprReference(new ExprPrimitive(new TypeString(line.getText())));
                reference.setLine(line);
                expression = new NodeExpression(reference);
            }
            validTrees.add(0, expression);
        }

        if (!validTrees.isEmpty()) {
            if (validTrees.size() == 1) {
                //System.out.println(debugOffset()+"Returning : "+validTrees.get(0));
                return validTrees.get(0);
            } else {
                Node result = new NodeSwitch(validTrees.toArray(new Node[0]));
                //System.out.println(debugOffset()+"Returning : "+result);
                return result;
            }
        }



        /*
         * We check if the expression contains operators, if so we split at each operator, and we return an ExprAlgebraicEvaluation expression.
         * /!\ In this case, we place Nodes in a list as if they were in an infixed notation, and then we later transform this with infixToRPN, and then with rpnToAST.
         */
        if (ScriptDecoder.containsOperator(expressionString)) {
            //System.out.println(debugOffset() + "Parsing as algebraic expression : " + expressionString);
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
            OperatorSplitResult operatorSplitResult = ScriptDecoder.splitAtOperators(operatorsBuildString);
            List<Token> tokens = Arrays.asList(operatorSplitResult.getExpressionTokens());
            List<Node> nodes = new ArrayList<>();
            int addedOperators = 0;
            for (Token token : tokens) {
                if (token.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                    nodes.add(new NodeParenthesis(EnumTokenType.LEFT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.RIGHT_PARENTHESIS) {
                    nodes.add(new NodeParenthesis(EnumTokenType.RIGHT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.EXPRESSION) {
                    Node node = parse(line.with(token.getExpressionString()), compilationContext, new Class[]{ScriptElement.class});
                    if (node == null)
                        return null;
                    nodes.add(node);
                } else if (token.getType() == EnumTokenType.OPERATOR) {
                    nodes.add(new NodeOperation(operators.get(addedOperators)));
                    addedOperators++;
                }
            }
            Node finalTree = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
            if (validTypes != null && isTypeValid(finalTree.getReturnType(), validTypes)) {
                return finalTree;
            }
        }
        //System.out.println(debugOffset() + "Returning null to " + line);
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
