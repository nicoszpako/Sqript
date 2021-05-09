package fr.nico.sqript.compiling;


import fr.nico.sqript.meta.Expression;

public class OperatorSplittedResult {

    private  Token[] operands;
    private  Integer[] operatorIndices;

    public OperatorSplittedResult(Token[] operands, Integer[] operatorIndices) {
        this.operands = operands;
        this.operatorIndices = operatorIndices;
    }

    public Token[] getOperands() {
        return operands;
    }

    public void setOperands(Token[] operands) {
        this.operands = operands;
    }

    public Integer[] getOperatorIndices() {
        return operatorIndices;
    }

    public void setOperatorIndices(Integer[] operatorIndices) {
        this.operatorIndices = operatorIndices;
    }

    public static class Token {

        EnumTokenType type;
        String expression;

        public Token(EnumTokenType type, String expression) {
            this.type = type;
            this.expression = expression;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", expression='" + expression + '\'' +
                    '}';
        }
    }

    public enum EnumTokenType {
        EXPRESSION,
        OPERATOR,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS
    }
}
