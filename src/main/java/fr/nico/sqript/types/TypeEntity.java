package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Type(name = "entity",
        parsableAs = {})
public class TypeEntity extends ScriptType<Entity> implements ILocatable {

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypeEntity(Entity entity) {
        super(entity);
    }

    @Override
    public Vec3d getVector() {
        return new Vec3d(getObject().posX,getObject().posY,getObject().posZ);
    }

}
