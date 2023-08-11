package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;


@Expression(name = "Network Expressions",
        features = {
                @Feature(name = "Element is synchronized", description = "Returns whether an element has been synchronized from the server.", examples = "\"my_key\" is synced\n", pattern = "[value] [of] {string} is (synced|synchronized)", type = "boolean"),
                @Feature(name = "Synchronized element", description = "Returns whether an element has been synchronized from the server.", examples = "synced value of \"my_key\"", pattern = "(synced|synchronized) value [of] {string}"),
        }
)
public class ExprNetwork extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                TypeString key = (TypeString) parameters[0];
                return new TypeBoolean(ScriptNetworkManager.syncValue.containsKey(key.getObject()));
            case 1:
                key = (TypeString) parameters[0];
                //System.out.println("Keys : "+ Arrays.toString(ScriptNetworkManager.syncValue.keySet().toArray(new String[0])));
                ScriptType result;
                if ((result = ScriptNetworkManager.get(key.getObject())) != null)
                    return result;
                else return new TypeNull();
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
