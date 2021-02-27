package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

@Type(name = "console",
        parsableAs = {})
public class TypeConsole extends ScriptType<MinecraftServer> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypeConsole(MinecraftServer sender) {
        super(sender);
    }


}
