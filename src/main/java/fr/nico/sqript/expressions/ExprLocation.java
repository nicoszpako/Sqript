package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.ArrayList;

@Expression(name = "Locations expressions",
        description = "Manipulate locations",
        examples = "x coordinate of [1,50,6]",
        patterns = {
                "x coord[inate] of {array}:number",
                "y coord[inate] of {array}:number",
                "z coord[inate] of {array}:number",
                "location of {element}|{element}'s location: array",
                "distance between {element} and {element}:number"

        }
)
public class ExprLocation extends ScriptExpression{
    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                ILocatable locatable = (ILocatable) parameters[0];
                return new TypeNumber((double) locatable.getPos().getX());
            case 1:
                locatable = (ILocatable) parameters[0];
                return new TypeNumber((double) locatable.getPos().getY());
            case 2:
                locatable = (ILocatable) parameters[0];
                return new TypeNumber((double) locatable.getPos().getZ());
            case 3:
                locatable = parameters[0] == null ? (ILocatable) parameters[1] : (ILocatable) parameters[0];
                return new TypeArray(SqriptUtils.locactionToArray(locatable.getPos()));
            case 4:
                ILocatable b1 = (ILocatable) parameters[0];
                ILocatable b2 = (ILocatable) parameters[1];
                return new TypeNumber(Math.sqrt(b1.getPos().distanceSq(b2.getPos())));
        }
        return null;
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        return false;
    }
}
