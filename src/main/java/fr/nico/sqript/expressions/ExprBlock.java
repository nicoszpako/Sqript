package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;

import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;

@Expression(name = "Block Expressions",
        description = "Manipulate blocks",
        examples = "block at [15,65,-25]",
        patterns = {
            "block at {array} [in world {number}]:block",
            "blocks in radius {number} around {element} [in world {number}]:array",
            "{resource} with metadata {number}:block",
            "{block} color [in world {number}]:number",
            "terrain height at {array} [in world {number}]:number"
        }
)
public class ExprBlock extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                World world;
                if(FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();                //System.out.println(Arrays.toString(parameters));
                if(parameters[0] instanceof TypeBlock) {
                    return parameters[0];
                }
                else if(parameters[0] instanceof ILocatable){
                    BlockPos pos = ((ILocatable) parameters[0]).getPos();
                    IBlockState b = world.getBlockState(pos);
                    return new TypeBlock(b,pos,world);
                }
                return null;
            case 1:
                worldId = parameters[1] == null ? 0 : (int) parameters[2].getObject();
                if(FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                BlockPos pos = ((ILocatable) parameters[1]).getPos();
                double radius = (double) parameters[0].getObject();
                ArrayList list = new ArrayList();
                for (int x = (int) (pos.getX()-radius); x < pos.getX()+radius; x++) {
                    for (int y = (int) (pos.getY()-radius); y < pos.getY()+radius; y++) {
                        for (int z = (int) (pos.getZ()-radius); z < pos.getZ()+radius; z++) {
                            if(Math.sqrt(Math.pow(x-pos.getX(),2)+Math.pow(y-pos.getY(),2)+Math.pow(z-pos.getZ(),2))<radius)
                                list.add(new TypeBlock(world.getBlockState(new BlockPos(x,y,z)),pos,world));
                        }
                    }
                }
                return new TypeArray(list);
            case 2:
                TypeResource resource = (TypeResource) parameters[0];
                int metadata = ((Double) parameters[1].getObject()).intValue();
                Block block = ForgeRegistries.BLOCKS.getValue(resource.getObject());
                IBlockState state = block.getStateFromMeta(metadata);
                return new TypeBlock(state);
            case 3:
                //System.out.println("Parameters : "+ Arrays.toString(parameters));
                worldId = parameters[1] == null ? 0 : (int) parameters[2].getObject();
                if(FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                TypeBlock blockstate = (TypeBlock) parameters[0];
                return new TypeNumber(blockstate.getObject().getMapColor(world,blockstate.getPos()).colorValue);
            case 4:
                worldId = parameters[1] == null ? 0 : (int) parameters[2].getObject();
                if(FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                ILocatable location = (ILocatable) parameters[0];
                Chunk chunk = world.getChunkFromBlockCoords(location.getPos());
                return new TypeNumber(chunk.getHeight(location.getPos()));
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public World getClientWorld(){
        return Minecraft.getMinecraft().world;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptTypeException {
        switch(getMatchedIndex()) {
            case 0:
                int worldId = getParameterOrDefault(parameters[1],0);
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
                //System.out.println("Setting block : "+state+" at "+pos);
                FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].setBlockState(pos, state);
                return true;
        }
        return false;
    }
}
