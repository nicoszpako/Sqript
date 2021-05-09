package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public class ParseResult {

    private final ScriptExpression expression;
    private final String[] arguments;

    public ParseResult(ScriptExpression expression, String[] arguments) {
        this.expression = expression;
        this.arguments = arguments;
    }

    public ScriptExpression getExpression() {
        return expression;
    }

    public String[] getArguments() {
        return arguments;
    }
}
