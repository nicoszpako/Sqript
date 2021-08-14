package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@Expression(name = "Player Expressions",
        description = "Manipulate the players",
        examples = "location of player",
        patterns = {
            "all players:array",
            "player (named|with username) {string}:player",
            "{+player}['s] name:player",
            "{+player}['s] health:number",
            "{+player}['s] (hunger|food):number",
            "{+player}['s] look vector:vector",
            "block {player} is looking at:block",
            "{player} is a player:boolean",
            "player:player"
        }
)
public class ExprPlayers extends ScriptExpression {

    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().player;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                synchronized (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()){
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
                return new TypeVector(Vec3d.fromPitchYaw(player.getPitchYaw().x,player.getRotationYawHead()));
            case 6:
                player = (EntityPlayer) parameters[0].getObject();
                World world = player.world;
                Vec3d vec3d = player.getPositionEyes(0);
                Vec3d vec3d1 = player.getLook(0);
                int blockReachDistance = 5;
                Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
                RayTraceResult result = world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
                if(result.typeOfHit == RayTraceResult.Type.BLOCK){
                    return new TypeBlock(world.getBlockState(result.getBlockPos()),result.getBlockPos(),world);
                }else
                    return new TypeNull();
            case 7:
                return new TypeBoolean(parameters[0].getObject() instanceof EntityPlayer);
            case 8:
                return new TypePlayer(getClientPlayer());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch(getMatchedIndex()) {
            case 3:
                EntityPlayer player = (EntityPlayer) parameters[0].getObject();
                float health = ((TypeNumber)to).getObject().floatValue();
                player.setHealth(health);
                break;
            case 4:
                player = (EntityPlayer) parameters[0].getObject();
                float hunger = ((TypeNumber)to).getObject().floatValue();
                player.getFoodStats().setFoodLevel((int) hunger);
                break;
        }
        return false;
    }
}
