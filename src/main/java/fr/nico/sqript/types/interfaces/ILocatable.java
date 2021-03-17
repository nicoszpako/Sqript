package fr.nico.sqript.types.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3f;

public interface ILocatable {

    public Vec3d getVector();
    default BlockPos getPos() {
        Vec3d vector = getVector();
        return new BlockPos(vector.x,vector.y,vector.z);
    }

}
