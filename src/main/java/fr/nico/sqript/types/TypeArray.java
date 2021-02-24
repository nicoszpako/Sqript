package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

@Type(name = "array",
        parsableAs = {}
)
public class TypeArray extends ScriptType<ArrayList<ScriptType>> implements ISerialisable,IIndexedCollection{

    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }


    @Override
    public String toString() {
        String s ="[";
        for(Object o : getObject()){
            s+=o+",";
        }
        s=replaceLast(s,",","");//Removing last comma
        return s+"]";
    }

    public TypeArray(){
        super(new ArrayList<>());
    }

    public TypeArray(ArrayList list){
        super(list);
    }


    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeArray.class, ScriptType.class,
                (a,b) -> {
                    TypeArray o = (TypeArray)a;
                    o.getObject().add(b);
                    return o;
                });
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for(ScriptType t : getObject()){
            if(t instanceof ISerialisable){
                ISerialisable i = (ISerialisable) t;
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setTag("value",i.write(new NBTTagCompound()));
                nbt.setString("type", Objects.requireNonNull(ScriptDecoder.getNameForType(t.getClass())));
                list.appendTag(nbt);
            }else{
                ScriptManager.log.error("Tried to register "+t.getClass().getSimpleName()+" but it's not serialisable.");
            }
        }
        compound.setTag("list",list);
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("list",9);
        l:for (Iterator<NBTBase> it = list.iterator(); it.hasNext(); ) {
            NBTTagCompound nbt = (NBTTagCompound) it.next();
            String typeName = nbt.getString("type");
            NBTTagCompound value = nbt.getCompoundTag("value");
            Class typeClass = ScriptDecoder.getType(typeName);
            assert typeClass != null;
            for(Constructor c : typeClass.getConstructors()){
                if(c.getParameterTypes()[0]!=String.class){
                    ScriptType t = null;
                    try {
                        t = (ScriptType) c.newInstance(new Object[]{null});
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if(!(t instanceof ISerialisable)) try {
                        throw new ScriptException.TypeNotSavableException(t.getClass());
                    } catch (ScriptException.TypeNotSavableException e) {
                        e.printStackTrace();
                    }
                    ISerialisable savable = (ISerialisable)t;
                    savable.read(value);
                    getObject().add((ScriptType) savable);
                    continue l;
                }
            }
            try {
                throw new Exception("Found no constructor for type : "+typeClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ScriptType<?> get(int index) {
        return getObject().get(index);
    }

    @Override
    public int size() {
        return getObject().size();
    }
}
