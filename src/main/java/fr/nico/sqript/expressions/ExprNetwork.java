package fr.nico.sqript.expressions;

import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;


@Expression(name = "Network Expressions",
        description = "Manipulate the network",
        examples = "synced value test",
        patterns = {
                "value [of] {string} is synced:boolean",
                "synced value [of] {string}:element"
        }
)
public class ExprNetwork extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                TypeString key = (TypeString) parameters[0];
                return new TypeBoolean(ScriptNetworkManager.syncValue.containsKey(key.getObject()));
            case 1:
                key = (TypeString) parameters[0];
                //System.out.println("Keys : "+ Arrays.toString(ScriptNetworkManager.syncValue.keySet().toArray(new String[0])));
                return ScriptNetworkManager.get(key.getObject());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
