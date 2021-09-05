package fr.nico.sqript.compiling.parsers;

import com.sun.org.apache.xpath.internal.ExpressionNode;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.expressions.ExprCompiledExpression;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.ExpressionDefinition;
import fr.nico.sqript.meta.MatchResult;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.ScriptParameterDefinition;
import fr.nico.sqript.structures.TransformedPattern;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptExpressionParser implements IParser {

    @Override
    public ExpressionTree parse(ScriptToken line, ScriptCompilationContext compilationContext) throws ScriptException {
        return parse(line, compilationContext, null);
    }

    public ExpressionTree parse(ScriptToken line, ScriptCompilationContext compilationContext, ScriptExpression parent) throws ScriptException {
        System.out.println();
        System.out.println("Parsing : " + line);

        String expressionString = line.getText();

        /*
         * We check if this line can be parsed by a simple parser.
         */
        for (IParser parser : ScriptDecoder.parsers) {
            ExpressionTree expressionTree;
            if ((expressionTree = parser.parse(line, compilationContext)) != null) {
                return expressionTree;
            }
        }

        /*
         * We loop through all registered expressions.
         */
        expressionLoop:
        for (ExpressionDefinition expressionDefinition : ScriptManager.expressions) {

            MatchResult matchResult = expressionDefinition.getMatchResult(expressionString);

            /*
             * If the expression matched, then we parse each found arguments, and we check if its type is convenient. If not, we check the next expression.
             */
            if (matchResult != null) {

                /*
                 * If we are parsing the exact same line that the parent on the exact same expression, then we continue to the next expression parsing check so that we don't enter an infinite loop.
                 */
                if (parent != null
                        && matchResult.getMatchedIndex() == parent.getMatchedIndex()
                        && expressionDefinition.getExpressionClass() == parent.getClass()
                        && line.equals(parent.getLine())) {
                    System.out.println("Not valid because identical to parent for "+expressionString);
                    continue;
                }

                try {

                    ScriptExpression expression = expressionDefinition.instanciate((ScriptToken) line.clone(), matchResult.getMatchedIndex(), matchResult.getMarks());
                    TransformedPattern transformedPattern = expressionDefinition.transformedPatterns[matchResult.getMatchedIndex()];
                    System.out.println("Found expression is : " + expression.toString());

                    String[] arguments = transformedPattern.getAllArguments(expressionString);
                    System.out.println("Expression sub arguments are : " + Arrays.toString(arguments));

                    /*
                     * We run the method validate() to check if all is ok before returning the final result.
                     */
                    if (expression.validate(arguments, line)) {


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
                        }

                        /*
                         * We parse each sub-argument, and we gather them all in an ExprComplexEvaluation expression.
                         */
                        ExpressionTree[] subExpressions = new ExpressionTree[parameters.size()];
                        int parameterIndex = 0;
                        for (String parameter : parameters) {
                            if (!parameter.isEmpty()) {
                                subExpressions[parameterIndex] = parse(line.with(parameter), compilationContext, expression);
                                if (subExpressions[parameterIndex] == null)
                                    continue expressionLoop;
                            } else {
                                subExpressions[parameterIndex] = null;
                            }
                            parameterIndex++;
                        }

                        /*
                         * We check if the given types are verified by the expression instance.
                         */
                        System.out.println("Sub expressions are: "+ Arrays.toString(subExpressions));
                        for (int i = 0; i < subExpressions.length; i++) {
                            ScriptParameterDefinition[] associatedParameterDefinition = transformedPattern.getTypes()[i];
                            if (subExpressions[i] != null) {
                                ScriptExpression subExpression = subExpressions[i].getExpression();
                                if (subExpression != null && !checkTypes(associatedParameterDefinition, subExpression)) {
                                    System.out.println("Not valid because incorrect types for "+expression);
                                    continue expressionLoop;
                                }
                            }
                        }
                        System.out.println("Returning : "+expression+" with "+Arrays.toString(subExpressions));
                        return new ExpressionTree(expression, subExpressions);

                    }else{
                        System.out.println("Not valid because not validated by expression.");
                    }


                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * We check if the expression contains operators, if so we split at each operator, and we return an ExprAlgebraicEvaluation expression.
         */
        if (ScriptDecoder.containsOperator(expressionString)) {
            System.out.println("Parsing as algebraic expression : "+expressionString);
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
            List<OperatorSplitResult.Token> tokens = Arrays.asList(operatorSplitResult.getExpressionTokens());
            List<ExpressionTree> nodes = new ArrayList<>();
            int addedOperators = 0;
            for (OperatorSplitResult.Token token : tokens) {
                if (token.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                    nodes.add(new ExpressionTree(EnumTokenType.LEFT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.RIGHT_PARENTHESIS) {
                    nodes.add(new ExpressionTree(EnumTokenType.RIGHT_PARENTHESIS));
                } else if (token.getType() == EnumTokenType.EXPRESSION) {
                    nodes.add(ScriptDecoder.parseExpressionTree(line.with(token.getExpressionString()), compilationContext));
                } else if (token.getType() == EnumTokenType.OPERATOR) {
                    nodes.add(new ExpressionTree(operators.get(addedOperators)));
                    addedOperators++;
                }
            }
            ExpressionTree finalTree = ExprCompiledExpression.rpnToAST(ExprCompiledExpression.infixToRPN(nodes));
            System.out.println("Returning : "+finalTree);
            return finalTree;
        }

        return null;
    }

    public boolean checkTypes(ScriptParameterDefinition[] possibleTypes, ScriptExpression expression) {
        boolean found = false;
        if (possibleTypes != null && expression.getReturnType() != null)
            for (ScriptParameterDefinition parameterDefinition : possibleTypes) {
                System.out.println("Checking if " + expression.getReturnType() + " is assignable from " + parameterDefinition.getTypeClass() + " : " + (expression.getReturnType().isAssignableFrom(parameterDefinition.getTypeClass())));
                if (parameterDefinition.getTypeClass().isAssignableFrom(expression.getReturnType())) {
                    found = true;
                    break;
                }
            }
        else
            found = true;
        return found;
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
