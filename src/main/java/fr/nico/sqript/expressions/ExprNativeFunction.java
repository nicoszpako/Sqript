package fr.nico.sqript.expressions;

import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

public class ExprNativeFunction extends ScriptExpression{

    //Special one (not registered commonly)

    public ScriptNativeFunction function;


    public ExprNativeFunction(ScriptNativeFunction function) {
        this.function=function;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        return function.get(context,parameters);
    }

    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
