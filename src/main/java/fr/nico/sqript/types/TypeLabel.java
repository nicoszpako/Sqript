package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Image;
import fr.nico.sqript.forge.gui.Label;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "label",
        parsableAs = {})
public class TypeLabel extends ScriptType<Label> {

    @Override
    public String toString() {
        return "Label "+getObject().getDisplayText();
    }

    public TypeLabel(Label label) {
        super(label);
    }


}
