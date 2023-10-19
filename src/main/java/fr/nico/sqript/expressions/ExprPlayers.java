package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.types.*;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;


@Expression(name = "Player Expressions",
        features = {
                @Feature(name = "All players", description = "Returns an array of all connected players.", examples = "all players", pattern = "all players", type = "array"),
                @Feature(name = "Player with username", description = "Returns the player with the given username.", examples = "player with username \"Player001\"", pattern = "player (named|with username) {string}", type = "player"),
                @Feature(name = "Player's name", description = "Returns the given player's name.", examples = "player's username", pattern = "{+player}['s] name", type = "string"),
                @Feature(name = "Player's health", description = "Returns the given player's health.", examples = "player's health", pattern = "{+player}['s] health", type = "number"),
                @Feature(name = "Player's hunger level", description = "Returns the given player's hunger level.", examples = "player's hunger", pattern = "{+player}['s] (hunger|food [level])", type = "number"),
                @Feature(name = "Player's look vector", description = "Returns the given player's look vector.", examples = "player's look vector smoothed with partial ticks", pattern = "{+player}['s] look vector [smoothed with {number}]", type = "vector"),
                @Feature(name = "Player's tool", description = "Returns the given player's tool.", examples = "{player}'s tool", pattern = "{+player}['s] tool", type = "item"),
                @Feature(name = "Block player is looking at", description = "Returns the block the given player is looking at", examples = "block player's looking at", pattern = "block {player} is looking at", type = "block"),
                @Feature(name = "Player check", description = "Check if the object is a player.", examples = "{player} is a player", pattern = "{player} is a player", type = "player"),
                @Feature(name = "Player", description = "Returns the player playing on the client side", examples = "player", pattern = "player", type = "player", side = fr.nico.sqript.structures.Side.CLIENT),
                @Feature(name = "Slot of player's inventory", description = "Returns the item in the given slot of a player's inventory", examples = "slot 4 of player's inventory", pattern = "slot {number} of {player}['s] inventory", type = "item"),
                @Feature(name = "Player's gamemode", description = "Returns the current gamemode of a player", examples = "if gamemode of player is 0: #Checks if player is in survival mode", pattern = "{player}['s] gamemode", type = "number"),
                @Feature(name = "Player's sneak", description = "Returns if the player is sneak.", examples = "if player is sneaking:", pattern = "{player}['s] is sneak[ing]", type = "boolean"),
                @Feature(name = "Player's armor level", description = "Returns the given player's armor level.", examples = "player's armor", pattern = "{+player}['s] (armor [level])", type = "number"),
                @Feature(name = "Player's bounding box", description = "Returns the bounding box of the player.", examples = "player bounding box", pattern = "{player}['s] bounding box", type = "axisalignedbb"),
                @Feature(name = "Player's rotation yaw", description = "Returns the rotation taw of the player.", examples = "player rotation yaw", pattern = "{player}['s] [head]['s] [rotation] yaw [smoothed with {number}]", type = "number"),
                @Feature(name = "Player's inventory", description = "Returns the player's inventory as an array of items.", examples = "player's inventory", pattern = "{player}['s] inventory", type = "array"),
                @Feature(name = "Player is connected", description = "Returns the whether the given player is connected.", examples = "player with username \"Player001\" is connected", pattern = "player (named|with username) {string} is connected", type = "boolean"),

        }
)
public class ExprPlayers extends ScriptExpression {

    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                if(FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning()){
                    synchronized (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()) {
                        TypeArray a = new TypeArray();
                        for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playerEntities) {
                            a.getObject().add(new TypePlayer(p));
                        }
                        return a;
                    }
                }else{
                    return new TypeArray();
                }
            case 1:
                TypeString s = (TypeString) parameters[0];
                if(s.getObject() == null)
                    return new TypeNull();
                if(FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning()) {
                    return new TypePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s.getObject()));
                }else
                    return new TypeNull();
            case 2:
                //System.out.println(parameters[0]);
                EntityPlayer player = (EntityPlayer) parameters[0].getObject();
                return new TypeString(player.getName());
            case 3:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeNumber(player.getHealth());
            case 4:
                player = (EntityPlayer) parameters[0].getObject();
                //System.out.println(player.getFoodStats().getFoodLevel());
                return new TypeNumber(player.getFoodStats().getFoodLevel());
            case 5:
                player = (EntityPlayer) parameters[0].getObject();
                float partialTicks = getParameterOrDefault(parameters[1],1d).floatValue();
                return new TypeVector(player.getLook(partialTicks));
            case 6:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeItemStack(player.getHeldItem(EnumHand.MAIN_HAND));
            case 7:
                player = (EntityPlayer) parameters[0].getObject();
                World world = player.world;
                Vec3d vec3d = player.getPositionEyes(0);
                Vec3d vec3d1 = player.getLook(0);
                int blockReachDistance = 5;
                Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
                RayTraceResult result = world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
                if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    return new TypeBlock(world.getBlockState(result.getBlockPos()), result.getBlockPos(), world);
                } else
                    return new TypeNull();
            case 8:
                return new TypeBoolean(parameters[0].getObject() instanceof EntityPlayer);
            case 9:
                return new TypePlayer(getClientPlayer());
            case 10:
                player = (EntityPlayer) parameters[1].getObject();
                int slot = ((Double) parameters[0].getObject()).intValue();
                return new TypeItemStack(player.inventory.getStackInSlot(slot));
            case 11:
                player = (EntityPlayer) parameters[0].getObject();
                if (player instanceof EntityPlayerMP) {
                    return new TypeNumber(((EntityPlayerMP) player).interactionManager.getGameType().getID());
                } else {
                    int gamemode = 0;
                    if (player.capabilities.isCreativeMode)
                        gamemode = 1;
                    else if (!player.capabilities.allowEdit)
                        gamemode = 2;
                    if (player.isSpectator())
                        gamemode = 3;
                    return new TypeNumber(gamemode);
                }
            case 12:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeBoolean(player.isSneaking());
            case 13:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeNumber(player.getTotalArmorValue());
            case 14:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeAxisAlignedBB(player.getEntityBoundingBox());
            case 15:
                player = (EntityPlayer) parameters[0].getObject();
                partialTicks = getParameterOrDefault(parameters[1],1d).floatValue();
                return new TypeNumber( player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks);
            case 16:
                player = (EntityPlayer) parameters[0].getObject();
                TypeArray array = new TypeArray();
                ArrayList<ScriptType<?>> items = new ArrayList<>();
                items.addAll(player.inventory.mainInventory.stream().map(TypeItemStack::new).collect(Collectors.toList()));
                items.addAll(player.inventory.armorInventory.stream().map(TypeItemStack::new).collect(Collectors.toList()));
                array.setObject(items);
                return array;
            case 17:
                s = (TypeString) parameters[0];
                if(s.getObject() == null)
                    return new TypeNull();
                if(FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning()) {
                    return new TypeBoolean(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s.getObject()) != null);
                }else
                    return new TypeBoolean(false);
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 3:
                EntityPlayer player = (EntityPlayer) parameters[0].getObject();
                float health = ((TypeNumber) to).getObject().floatValue();
                player.setHealth(health);
                return true;
            case 4:
                player = (EntityPlayer) parameters[0].getObject();
                float hunger = ((TypeNumber) to).getObject().floatValue();
                player.getFoodStats().setFoodLevel((int) hunger);
                return true;
            case 6:
                player = (EntityPlayer) parameters[0].getObject();
                ScriptType param = to;
                ItemStack item = null;
                if (param instanceof TypeResource) {
                    Item i = ForgeRegistries.ITEMS.getValue(((TypeResource) (param)).getObject());
                    if (i == null) {
                        i = Item.getItemFromBlock(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(((TypeResource) (param)).getObject())));
                        if (i == null){
                            ScriptManager.log.error("No item found for identifier : " + param.getObject().toString());
                            return true;
                        }
                    }
                    item = new ItemStack(i,1);
                }
                if (param instanceof TypeItemStack) {
                    item = ((TypeItemStack) (param)).getObject();
                }
                player.setHeldItem(EnumHand.MAIN_HAND, item);
                return true;
            case 10:
                player = (EntityPlayer) parameters[1].getObject();
                int slot = ((Double) parameters[0].getObject()).intValue();
                //System.out.println("Setting slot : "+slot+" of "+player+" to "+to);
                player.inventory.setInventorySlotContents(slot, (ItemStack) to.getObject());
                player.inventory.markDirty();
                return true;
            case 11:
                player = (EntityPlayer) parameters[0].getObject();
                int newgamemode = ((Double) to.getObject()).intValue();
                player.setGameType(GameType.getByID(newgamemode));
                return true;
        }
        return false;
    }
}
