package fr.nico.sqript.actions;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Action(name = "Network Actions",
        features = {
            @Feature(name = "Sync an element to all players", description = "Synchronise an element under the given key to all connected players, which can be used by client-side scripts.", examples = "sync true as \"game_started\" to all players #The synchronized variable \"game_started\" is now set to true for every players", pattern = "sync {element} as {string} to all players"),
            @Feature(name = "Sync an element to a players", description = "Synchronise an element under the given key to the given player, which can be used by client-side scripts.", examples = "sync $money[player] as \"my_money\" to player #The synchronized variable \"my_money\" now stores the content of the variable $money[player], stored on the server side.", pattern = "sync {element} as {string} to {player}")
        }
)
public class ActNetwork extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:

                ScriptType element = getParameters().get(0).get(context);
                TypeString key = (TypeString) getParameters().get(1).get(context);
                ScriptNetworkManager.sendToAll(key.getObject(),element);
                return;
            case 1:
                element = getParameters().get(0).get(context);
                key = (TypeString) getParameters().get(1).get(context);
                TypePlayer player = (TypePlayer) getParameters().get(2).get(context);
                ScriptNetworkManager.send(key.getObject(),element,player.getObject());
                return;
        }

    }
}