package fr.nico.sqript;

import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.lang.reflect.Constructor;

public class ScriptDataManager {

    public static void load() throws Exception {
        File f = new File(ScriptManager.scriptDir,"data.dat");
        if(!f.exists())return;
        NBTTagCompound nbt = CompressedStreamTools.read(f);
        assert nbt != null : "Wasn't able to read the data.dat file";
        for(String s : nbt.getKeySet()){
            NBTTagCompound n = nbt.getCompoundTag(s);
            String typeName = n.getString("type");
            NBTTagCompound value = n.getCompoundTag("value");
            ScriptType t = instanciateWithData(typeName,value);
            ScriptManager.GLOBAL_CONTEXT.put(new ScriptAccessor(t,s)); //We add the variable to the context
            throw new Exception("Found no constructor for type : "+typeName);
        }
    }

    public static void save() throws Exception {
        NBTTagCompound total = new NBTTagCompound();
        for(ScriptAccessor s : ScriptManager.GLOBAL_CONTEXT.getAccessors()){
            if(s.element instanceof ISerialisable){
                ISerialisable savable = (ISerialisable) s.element;
                String key = s.pattern.pattern();
                NBTTagCompound value = savable.write(new NBTTagCompound());
                String typeName = ScriptDecoder.getNameForType(s.element.getClass());
                NBTTagCompound toAdd = new NBTTagCompound();
                toAdd.setTag("value",value);
                toAdd.setString("type",typeName);
                total.setTag(key,toAdd);
                //System.out.println("Saved variable "+key+" as a "+typeName+" with value "+value);
            }else{
                throw new ScriptException.TypeNotSavableException(s.element.getClass());
            }
        }
        File f = new File(ScriptManager.scriptDir,"data.dat");
        CompressedStreamTools.write(total,f);
    }

    public static ScriptType instanciateWithData(String typeName, NBTTagCompound tag) throws Exception {
        Class typeClass = ScriptDecoder.getType(typeName);
        assert typeClass != null;
        for(Constructor c : typeClass.getConstructors()){
            if(typeClass != TypeString.class){
                if(c.getParameterTypes()[0]!=String.class) {
                    ScriptType t = (ScriptType) c.newInstance(new Object[]{null});
                    if (!(t instanceof ISerialisable))
                        throw new ScriptException.TypeNotSavableException(t.getClass());
                    ISerialisable savable = (ISerialisable) t;
                    savable.read(tag);
                    return t;
                }
            }else{
                    TypeString s = new TypeString("");
                    s.read(tag);
                    return s;
            }

        }
        throw new Exception("Found no constructor for type : "+typeClass);
    }

}
