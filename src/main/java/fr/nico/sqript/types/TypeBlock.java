package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

@Type(name = "block",
        parsableAs = {})
public class TypeBlock extends ScriptType<IBlockState> implements ILocatable {

    BlockPos pos;

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getBlock().getLocalizedName();
    }

    public TypeBlock(IBlockState block, BlockPos pos) {
        super(block);
        this.pos = pos;
    }

}
