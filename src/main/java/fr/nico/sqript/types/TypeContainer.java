package fr.nico.sqript.types;

import fr.nico.sqript.forge.gui.Container;
import fr.nico.sqript.forge.gui.Widget;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "container",
        parsableAs = {})
public class TypeContainer extends ScriptType<Container> {

    @Override
    public String toString() {
        return "container";
    }

    public TypeContainer(Container container) {
        super(container);
    }


}
