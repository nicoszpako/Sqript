package fr.nico.sqript.types;

import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.IComparable;

import javax.annotation.Nullable;
import java.util.Comparator;

public abstract class ScriptType<T>  extends ScriptElement<T>  {

    public ScriptType(){}

    public ScriptType(T object) {
        super(object);
    }

    @Override
    public boolean equals(Object o){
        return ((ScriptType) o).getObject() != null && ((ScriptType) o).getObject().equals(this.getObject());
    }

    @Override
    public int hashCode() {
        return getObject().hashCode();
    }

    /**
     * Gets the name of the given type class
     * @param type class
     * @return the name of the given type class
     */
    public static String getTypeName(Class<? extends ScriptType> type){
        if(type.isAnnotationPresent(Primitive.class)){
            return (type.getAnnotation(Primitive.class)).name();
        }else if(type.isAnnotationPresent(Type.class)){
            return (type.getAnnotation(Type.class)).name();
        }
        return null;
    }


}
