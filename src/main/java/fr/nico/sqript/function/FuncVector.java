package fr.nico.sqript.function;

import fr.nico.sqript.meta.Native;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeVector;
import fr.nico.sqript.types.primitive.TypeNumber;
import org.lwjgl.util.vector.Vector3f;

@Native(
        name="vector",
        definitions = {
                "scale(vector,number):vector",
                "dot(vector,vector):number",
                "cross(vector,vector):vector",
                "vector(number,number,number):vector"},
        description = "Vector things. All angles need to be in radians.",
        examples = {"vector(4,3,1)"})
public class FuncVector extends ScriptNativeFunction {


    public FuncVector(int matchedDefinition) {
        super(matchedDefinition);
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType... parameters) {
        switch(matchedDefinition) {
            case 0: {
                Vector3f v1 = (Vector3f) parameters[0].getObject();
                double s = (double) parameters[1].getObject();
                return new TypeVector((Vector3f) v1.scale((float) s));
            }
            case 1: {
                Vector3f v1 = (Vector3f) parameters[0].getObject();
                Vector3f v2 = (Vector3f) parameters[1].getObject();
                return new TypeNumber((double) Vector3f.dot(v1,v2));
            }
            case 2: {
                Vector3f v1 = (Vector3f) parameters[0].getObject();
                Vector3f v2 = (Vector3f) parameters[1].getObject();
                return new TypeVector(Vector3f.cross(v1,v2,null));
            }
            case 3: {
                double a = (double) parameters[0].getObject();
                double b = (double) parameters[1].getObject();
                double c = (double) parameters[2].getObject();
                return new TypeVector(new Vector3f((float)a,(float)b,(float)c));
            }
        }
        return null;
    }
}
