package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;

import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Expression(name = "Block Expressions",
        features = {
                @Feature(name = "Block at location", description = "Returns the block at the given location", examples = "block at player's location", pattern = "block at {array} [in world {number}]", type = "block"),
                @Feature(name = "Blocks in radius of location", description = "Returns blocks in a radius of the given location", examples = "block in a radius of 5 around player's location", pattern = "blocks in [a] radius [of] {number} [blocks] around {element} [in world {number}]", type = "array"),
                @Feature(name = "Block color", description = "Returns the color associated to the given block in a minecraft map.", examples = "color of minecraft:stone", pattern = "{block} color [in world {number}]", type = "color"),
                @Feature(name = "Terrain height", description = "Efficiently returns the terrain height at the given location.", examples = "terrain height at player's location", pattern = "terrain height at {array} [in world {number}]", type = "number"),
                @Feature(name = "Player's current world id", description = "Returns the id of the world the given player is. Setting a nbt tag must be done on server side or the effects won't persist.", examples = "send player's world id to player", pattern = "{player}['s] world id", type = "number"),
        }
)
public class ExprBlock extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch (getMatchedName()) {
            case "Block at location":
                int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                World world;
                if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();                //System.out.println(Arrays.toString(parameters));
                if (parameters[0] instanceof TypeBlock) {
                    return parameters[0];
                } else if (parameters[0] instanceof ILocatable) {
                    BlockPos pos = ((ILocatable) parameters[0]).getPos();
                    IBlockState b = world.getBlockState(pos);
                    return new TypeBlock(b, pos, world);
                }
                return null;
            case "Blocks in radius of location":
                worldId = parameters[2] == null ? 0 : (int) parameters[2].getObject();
                if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                BlockPos pos = ((ILocatable) parameters[1]).getPos();
                double radius = (double) parameters[0].getObject();
                ArrayList list = new ArrayList();
                for (int x = (int) (pos.getX() - radius); x < pos.getX() + radius; x++) {
                    for (int y = (int) (pos.getY() - radius); y < pos.getY() + radius; y++) {
                        for (int z = (int) (pos.getZ() - radius); z < pos.getZ() + radius; z++) {
                            if (Math.sqrt(Math.pow(x - pos.getX(), 2) + Math.pow(y - pos.getY(), 2) + Math.pow(z - pos.getZ(), 2)) < radius)
                                list.add(new TypeBlock(world.getBlockState(new BlockPos(x, y, z)), pos, world));
                        }
                    }
                }
                return new TypeArray(list);

            case "Block color":
                //System.out.println("Parameters : "+ Arrays.toString(parameters));
                worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                TypeBlock blockstate = (TypeBlock) parameters[0];
                return new TypeColor(new Color(blockstate.getObject().getMapColor(world, blockstate.getPos()).colorValue));
            case "Terrain height":
                worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                    world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                else
                    world = getClientWorld();
                ILocatable location = (ILocatable) parameters[0];
                Chunk chunk = world.getChunk(location.getPos());
                return new TypeNumber(chunk.getHeight(location.getPos()));
            case "Player's current world id":
                EntityPlayer player = (EntityPlayer) parameters[0].getObject();
                return new TypeNumber(Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().worlds).collect(Collectors.toList()).indexOf(player.world));
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptTypeException {
        switch (getMatchedName()) {
            case "Block at location":
                int worldId = getParameterOrDefault(parameters[1], 0);
                BlockPos pos = null;
                if (parameters[0] instanceof ILocatable) {
                    pos = ((ILocatable) parameters[0]).getPos();
                } else {
                    throw new ScriptException.ScriptTypeException(line, new Class[]{ILocatable.class}, parameters[0].getClass());
                }

                IBlockState state = null;
                if (to.getObject() instanceof IBlockState)
                    state = (IBlockState) to.getObject();
                else if (to.getObject() instanceof ResourceLocation)
                    state = ForgeRegistries.BLOCKS.getValue((ResourceLocation) to.getObject()).getDefaultState();
                assert state != null;
                //System.out.println("Setting block : "+state+" at "+pos);
                FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId].setBlockState(pos, state);
                return true;

        }
        return false;
    }
}
