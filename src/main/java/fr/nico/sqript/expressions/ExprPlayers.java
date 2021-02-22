package fr.nico.sqript.expressions;

import fr.nico.sqript.types.TypeBlockPos;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;


@Expression(name = "Player Expressions",
        description = "Manipulate the players",
        examples = "location of player",
        patterns = {
            "location of {player}:blockpos",
            "all players:array",
            "player (named|with username) {string}:player",
            "{+player}['s] name:player"
        }
)
public class ExprPlayers extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                EntityPlayer player = (EntityPlayer) parameters[0].getObject();
                return new TypeBlockPos(player.getPosition());
            case 1:
                synchronized (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()){
                    TypeArray a = new TypeArray();
                    for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playerEntities) {
                        a.getObject().add(new TypePlayer(p));
                    }
                    return a;
                }
            case 2:
                TypeString s = (TypeString) parameters[0];
                return new TypePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s.getObject()));
            case 3:
                player = (EntityPlayer) parameters[0].getObject();
                return new TypeString(player.getName());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
