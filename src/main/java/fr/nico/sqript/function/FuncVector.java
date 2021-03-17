package fr.nico.sqript.function;

import fr.nico.sqript.meta.Native;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeVector;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.util.math.Vec3d;
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
                Vec3d v1 = (Vec3d) parameters[0].getObject();
                double s = (double) parameters[1].getObject();
                return new TypeVector((Vec3d) v1.scale((float) s));
            }
            case 1: {
                Vec3d v1 = (Vec3d) parameters[0].getObject();
                Vec3d v2 = (Vec3d) parameters[1].getObject();
                return new TypeNumber(dot(v1,v2));
            }
            case 2: {
                Vec3d v1 = (Vec3d) parameters[0].getObject();
                Vec3d v2 = (Vec3d) parameters[1].getObject();
                return new TypeVector(cross(v1,v2,null));
            }
            case 3: {
                double a = (double) parameters[0].getObject();
                double b = (double) parameters[1].getObject();
                double c = (double) parameters[2].getObject();
                return new TypeVector(new Vec3d((float)a,(float)b,(float)c));
            }
        }
        return null;
    }

    public static double dot(Vec3d left, Vec3d right) {
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

    public static Vec3d cross(
            Vec3d left,
            Vec3d right,
            Vec3d dest)
    {

        if (dest == null)
            dest = new Vec3d(0,0,0);

        dest = new Vec3d(
                left.y * right.z - left.z * right.y,
                right.x * left.z - right.z * left.x,
                left.x * right.y - left.y * right.x
        );

        return dest;
    }
}
