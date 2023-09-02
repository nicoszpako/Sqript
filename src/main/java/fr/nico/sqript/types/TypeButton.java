package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Button;
import fr.nico.sqript.forge.gui.Container;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "button",
        parsableAs = {})
public class TypeButton extends ScriptType<Button> {

    @Override
    public String toString() {
        return "container";
    }

    public TypeButton(Button button) {
        super(button);
    }


}
