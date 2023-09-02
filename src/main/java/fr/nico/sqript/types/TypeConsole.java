package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.server.MinecraftServer;

@Type(name = "console",
        parsableAs = {})
public class TypeConsole extends ScriptType<MinecraftServer> {

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypeConsole(MinecraftServer sender) {
        super(sender);
    }


}
