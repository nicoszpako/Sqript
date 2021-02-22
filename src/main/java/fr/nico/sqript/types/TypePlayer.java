package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.entity.player.EntityPlayer;

@Type(name = "player",
        parsableAs = {})
public class TypePlayer extends ScriptType<EntityPlayer> {

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


}
