package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeString;
import org.lwjgl.util.vector.Vector3f;

@Type(name = "vector",
        parsableAs = {TypeString.class})
public class TypeVector extends ScriptType<Vector3f> {

    @Override
    public ScriptElement parse(String typeName) {
        if(typeName.equals("string"))return new TypeString(this.getObject().toString());
        return null;
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

    public TypeVector(Vector3f vec) {
        super(vec);
    }

}
