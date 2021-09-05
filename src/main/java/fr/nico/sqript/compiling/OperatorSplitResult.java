package fr.nico.sqript.compiling;


import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptOperator;

public class OperatorSplitResult {

    private  Token[] expressionTokens;
    private  Integer[] operatorIndices;

    public OperatorSplitResult(Token[] operands, Integer[] operatorIndices) {
        this.expressionTokens = operands;
        this.operatorIndices = operatorIndices;
    }

    public Token[] getExpressionTokens() {
        return expressionTokens;
    }

    public void setExpressionTokens(Token[] expressionTokens) {
        this.expressionTokens = expressionTokens;
    }

    public Integer[] getOperatorIndices() {
        return operatorIndices;
    }

    public void setOperatorIndices(Integer[] operatorIndices) {
        this.operatorIndices = operatorIndices;
    }

    public static class Token {

        private final EnumTokenType type;
        private final String expressionString;
        private ScriptOperator operator;

        public Token(EnumTokenType type, String expression) {
            this.type = type;
            this.expressionString = expression;
        }

        public Token(ScriptOperator operator) {
            this.operator = operator;
            this.type = EnumTokenType.OPERATOR;
            this.expressionString = null;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", expression='" + expressionString + '\'' +
                    '}';
        }

        public EnumTokenType getType() {
            return type;
        }

        public String getExpressionString() {
            return expressionString;
        }


    }


}
