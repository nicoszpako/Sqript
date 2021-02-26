package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.TypeSender;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

@Expression(name = "Block Expressions",
        description = "Manipulate blocks",
        examples = "block at [15,65,-25]",
        patterns = {
            "block at {array} [in world {number}]:block",
            "blocks in radius {number} around {array} [in world {number}]:block",
            "(location of {block}|{block}'s location):array",
        }
)
public class ExprBlock extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                BlockPos pos = SqriptUtils.arrayToLocation((ArrayList) parameters[0].getObject());
                IBlockState b = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].getBlockState(new BlockPos(pos));
                return new TypeBlock(b,pos);
            case 1:
                worldId = parameters[1] == null ? 0 : (int) parameters[2].getObject();
                pos = SqriptUtils.arrayToLocation((ArrayList) parameters[1].getObject());
                double radius = (double) parameters[0].getObject();
                ArrayList list = new ArrayList();
                for (int x = (int) (pos.getX()-radius); x < pos.getX()+radius; x++) {
                    for (int y = (int) (pos.getX()-radius); y < pos.getX()+radius; y++) {
                        for (int z = (int) (pos.getX()-radius); z < pos.getX()+radius; z++) {
                            if(Math.sqrt(Math.pow(x-pos.getX(),2)+Math.pow(y-pos.getY(),2)+Math.pow(z-pos.getZ(),2))<radius)
                                list.add(new TypeBlock(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].getBlockState(new BlockPos(x,y,z)),pos));
                        }
                    }
                }
                return new TypeArray(list);
            case 2:
                TypeBlock typeBlock = (TypeBlock) parameters[0];
                return new TypeArray(SqriptUtils.locactionToArray(typeBlock.getPos()));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch(getMatchedIndex()) {
            case 0:
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                BlockPos pos = SqriptUtils.arrayToLocation((ArrayList) parameters[0].getObject());
                IBlockState state = null;
                if(to.getObject() instanceof IBlockState)
                    state = (IBlockState) to.getObject();
                else if(to.getObject() instanceof ResourceLocation)
                    state = (IBlockState) ForgeRegistries.BLOCKS.getValue((ResourceLocation) to.getObject());
                assert state != null;
                FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].setBlockState(pos, state);
                return true;
        }
        return false;
    }
}
