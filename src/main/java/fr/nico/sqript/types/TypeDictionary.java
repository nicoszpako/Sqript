package fr.nico.sqript.types;

import fr.nico.sqript.ScriptDataManager;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

@Type(name = "dictionary",
        parsableAs = {}
)
public class TypeDictionary extends ScriptType<HashMap<ScriptType,ScriptType>> implements ISerialisable,IIndexedCollection{

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
        StringBuilder s = new StringBuilder("[");
        for(ScriptType a : getObject().keySet()){
            s.append("(").append(a.toString()).append(";").append(getObject().get(a).toString()).append("), ");
        };
        s = new StringBuilder(replaceLast(s.toString(), ", ", ""));//Removing last comma
        return s+"]";
    }

    public TypeDictionary(){
        super(new HashMap<>());
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeDictionary.class, ScriptType.class,
                (a,b) -> {
                    if(!(b instanceof TypeArray)){
                        ScriptManager.log.error("Only arrays with two coupled objects like [\"key\",8] can be added to dictionaries");
                        return null;
                    }
                    TypeDictionary o = (TypeDictionary)a;
                    TypeArray p = (TypeArray)b;
                    o.getObject().put(p.getObject().get(0),p.getObject().get(1));
                    return o;
                });
        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeDictionary.class, ScriptType.class,
                (a,b) -> {
                        TypeDictionary o = (TypeDictionary)a;
                        o.getObject().remove(b);
                        return o;
                });
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for(ScriptType t : getObject().keySet()){
            if(t instanceof ISerialisable){
                ISerialisable i = (ISerialisable) t;
                ISerialisable j = (ISerialisable) getObject().get(t);

                NBTTagCompound nbt = new NBTTagCompound();

                NBTTagCompound key = new NBTTagCompound();
                key.setTag("value",i.write(new NBTTagCompound()));
                key.setString("type", Objects.requireNonNull(ScriptDecoder.getNameForType(i.getClass())));

                NBTTagCompound value = new NBTTagCompound();
                value.setTag("value",j.write(new NBTTagCompound()));
                value.setString("type", Objects.requireNonNull(ScriptDecoder.getNameForType(j.getClass())));

                nbt.setTag("key",key);
                nbt.setTag("value",value);

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
            NBTTagCompound key = nbt.getCompoundTag("key");
            NBTTagCompound value = nbt.getCompoundTag("value");
            try {
                ScriptType skey = ScriptDataManager.instanciateWithData(key.getString("type"),key.getCompoundTag("value"));
                ScriptType svalue = ScriptDataManager.instanciateWithData(value.getString("type"),value.getCompoundTag("value"));
                getObject().put(skey,svalue);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public ScriptType<?> get(int index) {
        return getObject().values().toArray(new ScriptType[0])[index];
    }

    @Override
    public int size() {
        return getObject().size();
    }
}
