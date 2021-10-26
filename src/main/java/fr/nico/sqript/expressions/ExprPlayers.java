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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;


@Expression(name = "Player Expressions",
        features = {
                @Feature(name = "All players", description = "Returns an array of all connected players.", examples = "all players", pattern = "all players", type = "array"),
                @Feature(name = "Player with username", description = "Returns the player with the given username.", examples = "player with username \"Player001\"", pattern = "player (named|with username) {string}", type = "player"),
                @Feature(name = "Player's name", description = "Returns the given player's name.", examples = "player's username", pattern = "{+player}['s] name", type = "string"),
                @Feature(name = "Player's health", description = "Returns the given player's health.", examples = "player's health", pattern = "{+player}['s] health", type = "number"),
                @Feature(name = "Player's hunger level", description = "Returns the given player's hunger level.", examples = "player's hunger", pattern = "{+player}['s] (hunger|food [level])", type = "number"),
                @Feature(name = "Player's look vector", description = "Returns the given player's look vector.", examples = "player's look vector", pattern = "{+player}['s] look vector", type = "vector"),
                @Feature(name = "Player's tool", description = "Returns the given player's tool.", examples = "{player}'s tool", pattern = "{+player}['s] tool", type = "item"),
                @Feature(name = "Block player is looking at", description = "Returns the block the given player is looking at", examples = "block player's looking at", pattern = "block {player} is looking at", type = "block"),
                @Feature(name = "Player check", description = "Check if the object is a player.", examples = "{player} is a player", pattern = "{player} is a player", type = "player"),
                @Feature(name = "Player", description = "Returns the player playing on the client side", examples = "player", pattern = "player", type = "player", side = fr.nico.sqript.structures.Side.CLIENT),
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
                synchronized (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()) {
                    TypeArray a = new TypeArray();
                    for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playerEntities) {
                        a.getObject().add(new TypePlayer(p));
                    }
                    return a;
                }
            case 1:
                TypeString s = (TypeString) parameters[0];
                return new TypePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s.getObject()));
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
                return new TypeVector(Vec3d.fromPitchYaw(player.getPitchYaw().x, player.getRotationYawHead()));
            case 6:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeItem(player.getHeldItem(EnumHand.MAIN_HAND));
            case 7:
                player = (EntityPlayer) parameters[0].getObject();
                World world = player.world;
                Vec3d vec3d = player.getPositionEyes(0);
                Vec3d vec3d1 = player.getLook(0);
                int blockReachDistance = 5;
                Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
                RayTraceResult result = world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
                if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    return new TypeBlock(world.getBlockState(result.getBlockPos()), result.getBlockPos(), world);
                } else
                    return new TypeNull();
            case 8:
                return new TypeBoolean(parameters[0].getObject() instanceof EntityPlayer);
            case 9:
                return new TypePlayer(getClientPlayer());
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
                break;
            case 4:
                player = (EntityPlayer) parameters[0].getObject();
                float hunger = ((TypeNumber) to).getObject().floatValue();
                player.getFoodStats().setFoodLevel((int) hunger);
                break;
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
                            break;
                        }
                    }
                    item = new ItemStack(i,1);
                }
                if (param instanceof TypeItem) {
                    item = ((TypeItem) (param)).getObject();
                }
                player.setHeldItem(EnumHand.MAIN_HAND, item);
                break;
        }
        return false;
    }
}
