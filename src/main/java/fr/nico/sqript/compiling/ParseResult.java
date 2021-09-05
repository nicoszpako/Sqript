package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.TransformedPattern;

public class ParseResult {

    private final ScriptExpression expression;
    private ScriptExpression[] subExpressions;
    private final EnumResult result = EnumResult.PARTIAL;
    public ParseResult(ScriptExpression expression) {
        this.expression = expression;
    }

    public ScriptExpression getExpression() {
        return expression;
    }

    public ScriptExpression[] getSubExpressions() {
        return subExpressions;
    }

    public void setSubExpressions(ScriptExpression[] subExpressions) {
        this.subExpressions = subExpressions;
    }

    public EnumResult getResult() {
        return result;
    }

    public enum EnumResult {
        SUCCESS,
        PARTIAL,
        FAILURE;
    }



}
