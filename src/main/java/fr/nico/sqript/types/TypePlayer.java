package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Type(name = "player",
        parsableAs = {})
public class TypePlayer extends ScriptType<EntityPlayer> implements ILocatable {

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypePlayer(EntityPlayer player) {
        super(player);
    }


    @Override
    public Vec3d getVector() {
        return new Vec3d(getObject().posX,getObject().posY,getObject().posZ);
    }

}
