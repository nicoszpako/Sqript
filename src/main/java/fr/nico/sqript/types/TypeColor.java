package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.awt.*;
import java.io.File;

@Type(name = "color",
        parsableAs = {})
public class TypeColor extends ScriptType<Color> {

    @Override
    public String toString() {
        return this.getObject().toString();
    }

    public TypeColor(Color color) {
        super(color);
    }

    static {
        ScriptManager.registerTypeParser(TypeNumber.class, TypeColor.class, s->{
            long c = (long)s.getObject().doubleValue();
            int alpha = (int) ((c & 0xff000000) >> 24);
            int red = (int) ((c & 0x00ff0000) >> 16);
            int green = (int) ((c & 0x0000ff00) >> 8);
            int blue = (int) (c & 0x000000ff);
            return new TypeColor(new Color(red,green,blue,alpha));
        },0);
        ScriptManager.registerTypeParser(TypeColor.class, TypeNumber.class, s->new TypeNumber(s.getObject().getRGB()),0);
    }


}
