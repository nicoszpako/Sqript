package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector3f;

@Type(name = "vector",
        parsableAs = {TypeString.class})
public class TypeVector extends ScriptType<Vec3d> implements ILocatable {

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeVector .class, TypeVector.class, TypeVector.class,
                (a,b) -> new TypeVector(((TypeVector)a).getObject().add(((TypeVector)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeVector .class, TypeVector.class, TypeVector.class,
                (a,b) -> new TypeVector(((TypeVector)a).getObject().subtract(((TypeVector)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.MULTIPLY, TypeVector .class, TypeNumber.class, TypeVector.class,
                (a,b) -> new TypeVector(((TypeVector)a).getObject().scale(((TypeNumber)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.MULTIPLY, TypeNumber .class, TypeVector.class, TypeVector.class,
                (a,b) -> new TypeVector(((TypeVector)b).getObject().scale(((TypeNumber)a).getObject())));

        ScriptManager.registerUnaryOperation(ScriptOperator.MINUS_UNARY, TypeVector.class, TypeVector.class,
                (a,b) -> new TypeVector(((TypeVector)a).getObject().scale((-1))));

        ScriptManager.registerUnaryOperation(ScriptOperator.PLUS_UNARY, TypeVector.class, TypeVector.class,
                (a,b) -> a);

    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("vector(");
        sb.append(getObject().x);
        sb.append(", ");
        sb.append(getObject().y);
        sb.append(", ");
        sb.append(getObject().z);
        sb.append(')');
        return sb.toString();
    }

    public TypeVector(Vec3d vec) {
        super(vec);
    }

    @Override
    public Vec3d getVector() {
        return getObject();
    }
}
