package fr.nico.sqript;

import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import java.io.File;


public abstract class ScriptDataManager {

    public static void load() throws Exception {
        File f = new File(ScriptManager.scriptDir, "data.dat");
        if (!f.exists()) return;
        NBTTagCompound nbt = CompressedStreamTools.read(f);
        System.out.println("Loading saved data.");
        assert nbt != null : "Wasn't able to read the data.dat file";
        for (String s : nbt.getKeySet()) {
            System.out.println("Loading : " + s);
            NBTTagCompound n = nbt.getCompoundTag(s);
            String typeName = n.getString("type");
            NBTTagCompound value = n.getCompoundTag("value");
            System.out.println("Value : " + value + " as " + s);
            ScriptType t = instanciateWithData(typeName, value);
            ScriptManager.GLOBAL_CONTEXT.put(new ScriptTypeAccessor(t, s)); //We add the variable to the context
        }
        System.out.println("Global variables are " + ScriptManager.GLOBAL_CONTEXT.printVariables());

    }

    public static void save() throws Exception {
        NBTTagCompound total = new NBTTagCompound();
        for (ScriptTypeAccessor s : ScriptManager.GLOBAL_CONTEXT.getAccessors()) {
            if(s.element == null)
                continue;
            if (s.element instanceof ISerialisable) {
                ISerialisable savable = (ISerialisable) s.element;
                String key = s.key;
                NBTTagCompound value = savable.write(new NBTTagCompound());
                String typeName = ScriptDecoder.getNameOfType(s.element.getClass());
                NBTTagCompound toAdd = new NBTTagCompound();
                toAdd.setTag("value", value);
                toAdd.setString("type", typeName);
                total.setTag(key, toAdd);
            }
        }
        File f = new File(ScriptManager.scriptDir, "data.dat");
        CompressedStreamTools.write(total, f);
    }



    public static ScriptType instanciateWithData(String typeName, NBTTagCompound tag) throws Exception {
        //System.out.println("Instantiating : "+typeName+" with "+tag);
        Class<? extends ScriptElement> typeClass = ScriptDecoder.parseType(typeName);
        assert typeClass != null;
        if (typeClass != TypeString.class) {
            try {
                ScriptElement t = SqriptUtils.rawInstantiation(ScriptElement.class, typeClass);
                if (!(t instanceof ISerialisable))
                    throw new ScriptException.ScriptTypeNotSaveableException(typeName, t.getClass());
                ISerialisable savable = (ISerialisable) t;
                savable.read(tag);
                //System.out.println("Returning : "+t);
                return (ScriptType) t;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not instantiate type : " + typeName);
            }
        } else {
            TypeString s = new TypeString("");
            s.read(tag);
            return s;
        }
    }


}
