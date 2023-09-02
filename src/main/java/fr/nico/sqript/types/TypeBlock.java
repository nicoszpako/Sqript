package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;

@Type(name = "block",
        parsableAs = {})
public class TypeBlock extends ScriptType<IBlockState> implements ILocatable {

    BlockPos pos;
    World world;

    @Override
    public Vec3d getVector() {
        return new Vec3d(pos.getX(),pos.getY(),pos.getZ());
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof TypeBlock){
            return ((TypeBlock)(o)).getObject().getBlock() == getObject().getBlock();
        }else if(o instanceof TypeResource){
            return ((TypeResource)(o)).getObject().equals(getObject().getBlock().getRegistryName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pos);
    }

    @Override
    public String toString() {
        return this.getObject().toString();
    }

    public TypeBlock(IBlockState block) {
        this(block,null,null);
    }

    public TypeBlock(IBlockState block, BlockPos pos, World world) {
        super(block);
        this.pos = pos;
        this.world = world;
    }

    static {
        ScriptManager.registerTypeParser(TypeResource.class,TypeBlock.class,r->{
                Block block = ForgeRegistries.BLOCKS.getValue(r.getObject());
                IBlockState state = block.getDefaultState();
                return new TypeBlock(state);
        });
    }

}
