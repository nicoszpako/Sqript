package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptOperator;

public class Token {

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