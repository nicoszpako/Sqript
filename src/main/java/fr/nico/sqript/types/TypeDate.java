package fr.nico.sqript.types;


import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.primitive.TypeNumber;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Type(name="date",parsableAs = {})
public class TypeDate extends ScriptType<Long> {

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

    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeDate.class, TypeDate.class,
                (a, b) -> new TypeDate(((TypeDate) a).getObject() + ((TypeDate) b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeDate.class, TypeDate.class,
                (a, b) -> new TypeDate(((TypeDate) a).getObject() - ((TypeDate) b).getObject()));
    }

    }
