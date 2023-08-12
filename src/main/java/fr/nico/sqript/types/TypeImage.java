package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Button;
import fr.nico.sqript.forge.gui.Image;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "image",
        parsableAs = {})
public class TypeImage extends ScriptType<Image> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return "Image "+getObject().imageName;
    }

    public TypeImage(Image image) {
        super(image);
    }


}
