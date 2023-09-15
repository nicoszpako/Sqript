package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeVector;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.util.math.Vec3d;

@Expression(name = "Locations expressions",
        features = {
                @Feature(name = "X coordinate of location", description = "Returns the x coordinate of an element that can be associated to a location.", examples = "x coord of [1,52,47] #Returns 1", pattern = "x coord[inate] of {array}", type = "number"),
                @Feature(name = "Y coordinate of location", description = "Returns the y coordinate of an element that can be associated to a location.", examples = "y coord of [1,52,47] #Returns 52", pattern = "y coord[inate] of {array}", type = "number"),
                @Feature(name = "Z coordinate of location", description = "Returns the z coordinate of an element that can be associated to a location.", examples = "z coord of [1,52,47] #Returns 47", pattern = "z coord[inate] of {array}", type = "number"),
                @Feature(name = "Location of element", description = "Returns the location of an element that has a location.", examples = "player's location", pattern = "(location of {element}|{+element}'s location)", type = "array"),
                @Feature(name = "Distance between two locations", description = "Returns the distance between two locations.", examples = "distance between [1,42,56] and player's location", pattern = "distance between {element} and {element}", type = "number"),
                @Feature(name = "Vector of location", description = "Returns the vector from a location. Vectors handle addition (+), subtraction (-) and multiplication by a number (*).", examples = "vector of player's location", pattern = "(vector of {array}|{array} vector)", type = "vector"),
        }
)
public class ExprLocation extends ScriptExpression {
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
                Vec3d vector = locatable.getVector();
                return new TypeArray(SqriptUtils.locationToArray(vector.x, vector.y, vector.z));
            case 4:
                ILocatable b1 = (ILocatable) parameters[0];
                ILocatable b2 = (ILocatable) parameters[1];
                return new TypeNumber(Math.sqrt(b1.getPos().distanceSq(b2.getPos())));
            case 5:
                locatable = parameters[0] == null ? (ILocatable) parameters[1] : (ILocatable) parameters[0];
                return new TypeVector(locatable.getVector());
        }
        return null;
    }


    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        return false;
    }
}
