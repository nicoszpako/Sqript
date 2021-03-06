package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Server Expressions",
        description = "Manipulate the server side",
        examples = "number of connected players",
        patterns = {
            "number of [connected] players:number",
            "max number of [connected] players:number",
            "motd [of server]:string",
        },
        side = Side.SERVER
)
public class ExprServer extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                return new TypeNumber(FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount());
            case 1:
                return new TypeNumber(FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers());
            case 2:
                return new TypeString(FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
            case 1:
                return false;
            case 2:
               FMLCommonHandler.instance().getMinecraftServerInstance().setMOTD(to.getObject().toString());
               return true;
        }
        return false;
    }
}
