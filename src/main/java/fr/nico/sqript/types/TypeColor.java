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
        ScriptManager.registerTypeParser(TypeNumber.class, TypeColor.class, s->new TypeColor(new Color(s.getObject().intValue(),true)),0);
        ScriptManager.registerTypeParser(TypeColor.class, TypeNumber.class, s->new TypeNumber(s.getObject().getRGB()),0);
    }


}
