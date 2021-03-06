package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Arrays;


@Expression(name = "Player Expressions",
        description = "Manipulate the players",
        examples = "location of player",
        patterns = {
            "all players:array",
            "player (named|with username) {string}:player",
            "{+player}['s] name:player",
            "{+player}['s] health:number",
            "{+player}['s] (hunger|food):number",
        }
)
public class ExprPlayers extends ScriptExpression {

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
            case 4:
                player = (EntityPlayer) parameters[0].getObject();
                float hunger = ((TypeNumber)to).getObject().floatValue();
                player.getFoodStats().setFoodLevel((int) hunger);
        }
        return false;
    }
}
