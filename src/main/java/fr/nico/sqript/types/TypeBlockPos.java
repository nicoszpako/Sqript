package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

@Type(name = "blockpos",
        parsableAs = {})
public class TypeBlockPos extends ScriptType<BlockPos> implements ISerialisable {

    @Override
    public ScriptElement<?> parse(String string) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().toString();
    }

    public TypeBlockPos(BlockPos pos) {
        super(pos);
    }


    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setTag("value",NBTUtil.createPosTag(getObject()));
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        setObject(NBTUtil.getPosFromTag(compound.getCompoundTag("value")));
    }
}
