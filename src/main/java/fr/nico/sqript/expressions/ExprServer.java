package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Server Expressions",
        features = {
                @Feature(name = "Number of connected players", description = "Returns the number of current connected players.", examples = "number of connected players", pattern = "number of [connected] players", type = "number", side = Side.SERVER),
                @Feature(name = "Maximum number of connected players", description = "Returns the number of current connected players.", examples = "max number of players", pattern = "max[imum] number of [connected] players", type = "number", side = Side.SERVER),
                @Feature(name = "Motd of server", description = "Returns the motd of the server.", examples = "set motd of server to \"My server\"", pattern = "motd [of server]", type = "string", side = Side.SERVER)
        }
)
public class ExprServer extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch (getMatchedIndex()) {
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
        switch (getMatchedIndex()) {
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
