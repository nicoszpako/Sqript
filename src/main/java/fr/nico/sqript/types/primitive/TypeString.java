package fr.nico.sqript.types.primitive;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.ISerialisable;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

@Primitive(name = "string",
        parsableAs = {},
        pattern = ScriptDecoder.CAPTURE_BETWEEN_QUOTES
)
public class TypeString extends PrimitiveType<String> implements ISerialisable {

    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        switch(typeName){
            case "number":
                return new TypeNumber(typeName);
        }
        return null;
    }

    public TypeString(String parameter){
        super(parameter);
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeString.class, ScriptElement.class, TypeString.class,
                (a, b) -> new TypeString((a==null ? "null" : a.getObject()) + (b==null ? "null" : b.toString())));

        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, ScriptElement.class, TypeString.class, TypeString.class,
                (a, b) -> new TypeString((a==null ? "null" : a.toString()) + (b==null ? "null" : b.getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.MT, TypeString.class, TypeString.class, TypeString.class,
                (a, b) -> new TypeBoolean(((TypeString) a).getObject().compareTo(((TypeString) b).getObject()) > 0));

        ScriptManager.registerBinaryOperation(ScriptOperator.MTE, TypeString.class, TypeString.class, TypeString.class,
                (a, b) -> new TypeBoolean(((TypeString) a).getObject().compareTo(((TypeString) b).getObject()) >= 0));

        ScriptManager.registerBinaryOperation(ScriptOperator.LT, TypeString.class, TypeString.class, TypeString.class,
                (a, b) -> new TypeBoolean(((TypeString) a).getObject().compareTo(((TypeString) b).getObject()) < 0));

        ScriptManager.registerBinaryOperation(ScriptOperator.LTE, TypeString.class, TypeString.class, TypeString.class,
                (a, b) -> new TypeBoolean(((TypeString) a).getObject().compareTo(((TypeString) b).getObject()) <= 0));

    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setString("object",getObject());
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        setObject(compound.getString("object"));
    }

}
