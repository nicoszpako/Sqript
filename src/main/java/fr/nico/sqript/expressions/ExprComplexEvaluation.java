package fr.nico.sqript.expressions;


import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

public class ExprComplexEvaluation extends ScriptExpression {

    private final ScriptExpression mainExpression;
    private final ScriptExpression[] subExpressions;

    public ExprComplexEvaluation(ScriptExpression mainExpression, ScriptExpression[] subExpressions) {
        this.mainExpression = mainExpression;
        this.subExpressions = subExpressions;
    }

    public ScriptExpression getMainExpression() {
        return mainExpression;
    }

    public ScriptExpression[] getSubExpressions() {
        return subExpressions;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException, ScriptException.ScriptInterfaceNotImplementedException {
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        return false;
    }
}
