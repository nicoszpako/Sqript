package fr.nico.sqript.actions;

import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;

@Action(name = "Network Actions",
        description ="Network related actions",
        examples = {"sync dictionary with [a,b] to player with username \"Player665\" as \"a\""
        },
        patterns = {
                "sync {element} as {string} to all players",
                "sync {element} as {string} to {player}"
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