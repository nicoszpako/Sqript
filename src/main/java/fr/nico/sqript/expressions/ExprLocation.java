package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.ArrayList;

@Expression(name = "Locations expressions",
        description = "Manipulate locations",
        examples = "x coordinate of [1,50,6]",
        patterns = {
                "x coord[inate] of {array}:number",
                "y coord[inate] of {array}:number",
                "z coord[inate] of {array}:number",
        }
)
public class ExprLocation extends ScriptExpression{
    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                ArrayList array = (ArrayList) parameters[0].getObject();
                return new TypeNumber((Double) array.get(0));
            case 1:
                array = (ArrayList) parameters[0].getObject();
                return new TypeNumber((Double) array.get(1));
            case 2:
                array = (ArrayList) parameters[0].getObject();
                return new TypeNumber((Double) array.get(2));
        }
        return null;
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        return false;
    }
}
