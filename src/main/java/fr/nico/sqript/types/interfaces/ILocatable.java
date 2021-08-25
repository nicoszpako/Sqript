package fr.nico.sqript.types.interfaces;

import fr.nico.sqript.meta.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3f;

@Type(name = "location",
        parsableAs = {})
public interface ILocatable {

    public Vec3d getVector();
    default BlockPos getPos() {
        Vec3d vector = getVector();
        return new BlockPos(vector.x,vector.y,vector.z);
    }

}
