package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Image;
import fr.nico.sqript.meta.Type;

import java.awt.image.BufferedImage;

@Type(name = "image",
        parsableAs = {})
public class TypeImage extends ScriptType<BufferedImage> {

    @Override
    public String toString() {
        return "Image sized "+getObject().getWidth()+"x"+getObject().getHeight();
    }

    public TypeImage(BufferedImage image) {
        super(image);
    }


}
