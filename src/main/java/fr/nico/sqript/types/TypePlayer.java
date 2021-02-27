package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

@Type(name = "player",
        parsableAs = {})
public class TypePlayer extends ScriptType<EntityPlayer> implements ILocatable {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypePlayer(EntityPlayer player) {
        super(player);
    }


    @Override
    public BlockPos getPos() {
        return getObject().getPosition();
    }
}
