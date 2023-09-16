package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Image;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "image widget",
        parsableAs = {})
public class TypeImageWidget extends ScriptType<Image> {

    @Override
    public String toString() {
        return "Image widget from "+getObject().imageName;
    }

    public TypeImageWidget(Image image) {
        super(image);
    }


}
