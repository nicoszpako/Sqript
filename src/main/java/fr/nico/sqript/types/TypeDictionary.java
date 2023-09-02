package fr.nico.sqript.types;

import fr.nico.sqript.ScriptDataManager;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.IIndexedCollection;
import fr.nico.sqript.types.interfaces.ISerialisable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Type(name = "dictionary",
        parsableAs = {}
)
public class TypeDictionary extends ScriptType<HashMap<ScriptType,ScriptType>> implements ISerialisable, IIndexedCollection {

    public TypeDictionary(LinkedHashMap<ScriptType, ScriptType> map) {
        super(map);
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
        if(getObject() != null)
            for(ScriptType a : getObject().keySet()){
                s.append("(").append(a.toString()).append(";").append(getObject().get(a).toString()).append("), ");
            };
        s = new StringBuilder(replaceLast(s.toString(), ", ", ""));//Removing last comma
        return s+"]";
    }

    public TypeDictionary(){
        super(new LinkedHashMap<>());
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeDictionary.class, ScriptElement.class, TypeDictionary.class,
                (a,b) -> {
                    if(!(b instanceof TypeArray)){
                        ScriptManager.log.error("Only arrays with two coupled objects like [\"key\",8] can be added to dictionaries");
                        return null;
                    }
                    TypeDictionary o = (TypeDictionary)a;
                    TypeArray p = (TypeArray)b;
                    o.getObject().put(p.getObject().get(0),p.getObject().get(1));
                    return o;
                },2);
        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeDictionary.class, ScriptElement.class, TypeDictionary.class,
                (a,b) -> {
                        TypeDictionary o = (TypeDictionary)a;
                        o.getObject().remove(b);
                        return o;
                },2);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        if(getObject() == null)
            setObject(new HashMap<>());
        for(ScriptType t : getObject().keySet()){
            if(t instanceof ISerialisable){
                ISerialisable i = (ISerialisable) t;
                ISerialisable j = (ISerialisable) getObject().get(t);

                NBTTagCompound nbt = new NBTTagCompound();

                NBTTagCompound key = new NBTTagCompound();
                key.setTag("value",i.write(new NBTTagCompound()));
                key.setString("type", Objects.requireNonNull(ScriptDecoder.getNameOfType(i.getClass())));

                NBTTagCompound value = new NBTTagCompound();
                value.setTag("value",j.write(new NBTTagCompound()));
                value.setString("type", Objects.requireNonNull(ScriptDecoder.getNameOfType(j.getClass())));

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
        //System.out.println("Reading : "+compound+" getObject is : "+(getObject() == null));
        NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
        //System.out.println("List is : "+list);
        if(getObject() == null)
            setObject(new HashMap<>());
        for (NBTBase nbtBase : list) {
            //System.out.println("Adding : "+nbtBase);
            NBTTagCompound nbt = (NBTTagCompound) nbtBase;
            NBTTagCompound key = nbt.getCompoundTag("key");
            NBTTagCompound value = nbt.getCompoundTag("value");
            try {
                ScriptType skey = ScriptDataManager.instanciateWithData(key.getString("type"), key.getCompoundTag("value"));
                ScriptType svalue = ScriptDataManager.instanciateWithData(value.getString("type"), value.getCompoundTag("value"));
                getObject().put(skey, svalue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("Map is now : "+ getObject());
        }
    }


    @Override
    public boolean contains(ScriptType<?> t) {
        return getObject().containsValue(t);
    }

    @Override
    public ScriptType<?> get(int index) {
        return getObject().values().toArray(new ScriptType[0])[index];
    }

    @Override
    public IIndexedCollection sort(int mode) {
        boolean ascending = ((mode >> 0) & 1) == 1 || ((mode >> 1) & 1) == 0;
        boolean byValue = ((mode >> 2) & 1) == 1 || ((mode >> 3) & 1) == 0;
        int n_mt = ascending ? 1 :-1;
        int n_lt = ascending ? -1 :1;
        //System.out.println("mode  : "+Integer.toBinaryString(mode));
        //System.out.println("ascending : "+ascending+" byValue : "+byValue+" n_mt : "+n_mt+" n_lt : "+n_lt);
        setObject(getObject().entrySet()
                    .stream()
                    .sorted(byValue ? Map.Entry.comparingByValue((o1, o2) -> {
                        IOperation lt = ScriptManager.getBinaryOperation(o1.getClass(),o2.getClass(), ScriptOperator.LT).getOperation();
                        IOperation mt = ScriptManager.getBinaryOperation(o1.getClass(),o2.getClass(), ScriptOperator.MT).getOperation();
                        if(lt != null && mt != null){
                            //System.out.println("operating "+o1+"<"+o2+" it's : "+(Boolean)(lt.operate(o1,o2).getObject()));
                            if((Boolean)(lt.operate(o1,o2).getObject())){
                                return n_lt;
                            }else if((Boolean)(mt.operate(o1,o2).getObject())){
                                return n_mt;
                            }else
                                return 0;
                        }
                        return 0;
                    }) : Map.Entry.comparingByKey((o1, o2) -> {
                        IOperation lt = ScriptManager.getBinaryOperation(o1.getClass(),o2.getClass(), ScriptOperator.LT).getOperation();
                        IOperation mt = ScriptManager.getBinaryOperation(o1.getClass(),o2.getClass(), ScriptOperator.MT).getOperation();
                        if(lt != null && mt != null){
                            if((Boolean)(lt.operate(o1,o2).getObject())){
                                return n_lt;
                            }else if((Boolean)(mt.operate(o1,o2).getObject())){
                                return n_mt;
                            }else
                                return 0;
                        }
                        return 0;
                    }))
                    .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new)));
        return this;
    }

    @Override
    public IIndexedCollection copy() {
        return new TypeDictionary(new LinkedHashMap<>(getObject()));
    }

    @Override
    public int size() {
        return getObject().size();
    }
}
