package fr.nico.sqript.expressions;

import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

public class ExprFunction extends ScriptExpression{

    public ScriptFunctionalBlock function;

    public ExprFunction(ScriptFunctionalBlock function) {
        this.function=function;
    }

    @Override
    public ScriptType<?> get(ScriptContext context, ScriptType[] parameters) throws ScriptException {

        return function.get(context,parameters);
    }

    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) {
        return false;
    }

    @Override
    public String toString() {
        return "ExprFunction:" +function.name+" ";

    }
}
