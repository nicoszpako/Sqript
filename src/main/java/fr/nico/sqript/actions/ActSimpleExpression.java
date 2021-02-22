package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptContext;

public class ActSimpleExpression extends ScriptAction {

    ScriptExpression exp;

    public ActSimpleExpression(ScriptExpression exp) {
        this.exp = exp;
    }

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        exp.get(context);
    }
}
