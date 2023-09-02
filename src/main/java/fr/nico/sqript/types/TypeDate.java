package fr.nico.sqript.types;


import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.IFormatable;
import fr.nico.sqript.types.primitive.TypeNumber;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Type(name="date",parsableAs = {})
public class TypeDate extends ScriptType<Long> implements IFormatable {

    public Date toDate(){
        return new Date(getObject());
    }

    public TypeDate(long rawMilliseconds) {
        super(rawMilliseconds);
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy 'y' MM 'M' d 'd' HH 'h' mm 'm and' ss 's'");
        return format.format(toDate());
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeDate.class, TypeDate.class, TypeDate.class,
                (a, b) -> new TypeDate(((TypeDate) a).getObject() + ((TypeDate) b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeDate.class, TypeDate.class, TypeDate.class,
                (a, b) -> new TypeDate(((TypeDate) a).getObject() - ((TypeDate) b).getObject()));
    }

    @Override
    public String format(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(getObject()));
    }
}
