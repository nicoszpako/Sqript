package fr.nico.sqript.expressions;

import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.PrimitiveType;


public class ExprPrimitive extends ScriptExpression{

    //Special one (not registered commonly)
    //Permet d'instancier des TypePrimitive


    Class<? extends PrimitiveType> type;

    PrimitiveType primitive;

    public ExprPrimitive(PrimitiveType primitive) {
       this.primitive=primitive;
       this.type=primitive.getClass();
    }

    public Class<? extends ScriptElement> getReturnType() {
        return type;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        return primitive;
    }

    @Override
    public boolean set(ScriptContext context,ScriptType to, ScriptType[] parameters) { //set "test" to "a" doesn't have any sense
        return false;
    }

    @Override
    public String toString() {
        return "ExprPrimitive("+primitive.getType().getSimpleName()+")"+"="+primitive;
    }
}
