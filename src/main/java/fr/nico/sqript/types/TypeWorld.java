package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

@Type(name = "world",
        parsableAs = {})
public class TypeWorld extends ScriptType< World > {

    @Override
    public String toString() {
        return this.getObject().getWorldInfo().getWorldName();
    }

    public TypeWorld(World world) {
        super(world);
    }

}
