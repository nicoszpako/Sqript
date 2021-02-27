package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;

import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
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
            "blocks in radius {number} around {element} [in world {number}]:array",
        }
)
public class ExprBlock extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                if(parameters[0] instanceof TypeBlock) {
                    return parameters[0];
                }
                else if(parameters[0] instanceof ILocatable){
                    BlockPos pos = ((ILocatable) parameters[0]).getPos();
                    IBlockState b = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].getBlockState(new BlockPos(pos));
                    return new TypeBlock(b,pos);
                }
                return null;
            case 1:
                worldId = parameters[1] == null ? 0 : (int) parameters[2].getObject();
                BlockPos pos = ((ILocatable) parameters[1]).getPos();
                double radius = (double) parameters[0].getObject();
                ArrayList list = new ArrayList();
                for (int x = (int) (pos.getX()-radius); x < pos.getX()+radius; x++) {
                    for (int y = (int) (pos.getY()-radius); y < pos.getY()+radius; y++) {
                        for (int z = (int) (pos.getZ()-radius); z < pos.getZ()+radius; z++) {
                            if(Math.sqrt(Math.pow(x-pos.getX(),2)+Math.pow(y-pos.getY(),2)+Math.pow(z-pos.getZ(),2))<radius)
                                list.add(new TypeBlock(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].getBlockState(new BlockPos(x,y,z)),pos));
                        }
                    }
                }
                return new TypeArray(list);

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptTypeException {
        switch(getMatchedIndex()) {
            case 0:
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                BlockPos pos = null;
                if(parameters[0] instanceof ILocatable) {
                    pos = ((ILocatable) parameters[0]).getPos();
                }else{
                    throw new ScriptException.ScriptTypeException(line,ILocatable.class,parameters[0].getClass());
                }

                IBlockState state = null;
                if(to.getObject() instanceof IBlockState)
                    state = (IBlockState) to.getObject();
                else if(to.getObject() instanceof ResourceLocation)
                    state = ForgeRegistries.BLOCKS.getValue((ResourceLocation) to.getObject()).getDefaultState();
                assert state != null;

                FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].setBlockState(pos, state);
                return true;
        }
        return false;
    }
}
