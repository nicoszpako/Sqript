package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptOperator;

public class ExpressionToken {

    private final EnumTokenType type;
    private final String expressionString;
    private ScriptOperator operator;

    public ExpressionToken(EnumTokenType type, String expression) {
        this.type = type;
        this.expressionString = expression;
    }

    public ExpressionToken(ScriptOperator operator) {
        this.operator = operator;
        this.type = EnumTokenType.OPERATOR;
        this.expressionString = null;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ",expression=" + (expressionString == null ? operator.toString() : expressionString) + '\'' +
                '}';
    }

    public EnumTokenType getType() {
        return type;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public ScriptOperator getOperator() {
        return operator;
    }
}