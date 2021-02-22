package fr.nico.sqript.expressions;

import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

public class ExprResult extends ScriptExpression{

    //Only useful to wrap a variable that cannot be settable
    //Special one (not registered and no pattern)

    public ScriptType var;

    public ExprResult(ScriptType var) {
        this.var=var;
    }

    @Override
    public Class<? extends ScriptElement> getReturnType() {
        return var.getClass();
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        return var;
    }

    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
