package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.command.ICommandSender;

@Type(name = "sender",
        parsableAs = {})
public class TypeSender extends ScriptType<ICommandSender> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypeSender(ICommandSender sender) {
        super(sender);
    }


}
