package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.IIndexedCollection;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

@Type(name = "array",
        parsableAs = {}
)
public class TypeArray extends ScriptType<ArrayList<ScriptType<?>>> implements ISerialisable, IIndexedCollection, ILocatable {

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

    public TypeArray(ArrayList<ScriptType<?>> list){
        super(list);
    }


    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeArray.class, ScriptElement.class, TypeArray.class,
                (a,b) -> {
                    TypeArray o = (TypeArray)a;
                    o.getObject().add(b);
                    return o;
                },2);
        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeArray.class, ScriptElement.class, TypeArray.class,
                (a,b) -> {
                    TypeArray o = (TypeArray)a;
                    o.getObject().remove(b);
                    return o;
                },2);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for(ScriptType t : getObject()){
            if(t instanceof ISerialisable){
                ISerialisable i = (ISerialisable) t;
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setTag("value",i.write(new NBTTagCompound()));
                nbt.setString("type", Objects.requireNonNull(ScriptDecoder.getNameOfType(t.getClass())));
                list.appendTag(nbt);
            }else{
                ScriptManager.log.error("Tried to register "+t.getClass().getSimpleName()+" but it's not serialisable.");
            }
        }
        compound.setTag("list",list);
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) throws ScriptException {
        //System.out.println("Reading : "+compound);
        setObject(new ArrayList<>());
        NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
        l:for (Iterator<NBTBase> it = list.iterator(); it.hasNext(); ) {
            NBTTagCompound nbt = (NBTTagCompound) it.next();
            String typeName = nbt.getString("type");
            NBTTagCompound value = nbt.getCompoundTag("value");
            Class typeClass = ScriptDecoder.parseType(typeName);
            //System.out.println("Typeclass is : "+typeClass);
            assert typeClass != null;
            //System.out.println("Iterating on : "+typeName+" "+value);
            ScriptElement t = null;
            try {
                t = (ScriptElement) SqriptUtils.rawInstantiation(ScriptElement.class,typeClass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            if (!(t instanceof ISerialisable))
                throw new ScriptException.ScriptTypeNotSaveableException(typeName,t.getClass());
            ISerialisable savable = (ISerialisable) t;
            savable.read(value);
            getObject().add((ScriptType) savable);
        }
        //System.out.println("Read finished : "+ this);
    }

    @Override
    public boolean contains(ScriptType<?> t) {
        return getObject().contains(t);
    }

    @Override
    public ScriptType<?> get(int index) {
        if(index < getObject().size())
            return getObject().get(index);
        else return new TypeNull();
    }

    public void add(ScriptType<?> type){
        getObject().add(type);
    }

    @Override
    public IIndexedCollection sort(int mode) {
        boolean ascending = (mode & 1) == 1 || ((mode >> 1) & 1) == 0;
        int n_mt = ascending ? 1 :-1;
        int n_lt = ascending ? -1 :1;
        TypeArray copy = (TypeArray) copy();
        Collections.sort(copy.getObject(),(o1, o2) -> {
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
        });
        return copy;


    }

    @Override
    public IIndexedCollection copy() {
        return new TypeArray(new ArrayList(getObject()));
    }

    @Override
    public int size() {
        return getObject().size();
    }

    @Override
    public Vec3d getVector() {
        return SqriptUtils.arrayToLocation(getObject());
    }

    public NBTBase toNbtList() {
        NBTTagList list = new NBTTagList();
        for(ScriptType t : getObject()){
            //System.out.println("Looping on :" +t);
            if(t instanceof ISerialisable){
                ISerialisable i = (ISerialisable) t;
                   list.appendTag(i.write(new NBTTagCompound()));
            }else{
                ScriptManager.log.error("Tried to register "+t.getClass().getSimpleName()+" but it's not serialisable.");
            }
        }
        return list;
    }
}
