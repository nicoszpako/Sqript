package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.TypeNBTTagCompound;
import fr.nico.sqript.types.primitive.TypeString;

import java.util.Collection;

@Expression(name = "Dictionaries",
        features = {
                @Feature(name = "Dictionary", description = "Returns a new dictionary.", examples = "set {my_dictionary} to a new dictionary", pattern = "[a] [new] dictionary", type = "dictionary"),
                @Feature(name = "Dictionary from an array of key-value couples", description = "Returns a new dictionary based on elements of an array that represent key-value couples.", examples = "set {my_dictionary} to a new dictionary with [[\"Key 1\",\"Value 1\"]]", pattern = "[a] [new] dictionary with {array*}", type = "dictionary"),
                @Feature(name = "Keys of dictionary/nbt", description = "Returns an array containing the keys of the given dictionary or nbt.", examples = "keys of dictionary with [[\"Key 1\",\"Value 1\"]] #[\"Key 1\"]", pattern = "keys of {dictionary|nbttagcompound}", type = "array"),
        }
)
public class ExprDictionaries extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                return new TypeDictionary();
            case 1:
                TypeDictionary d = new TypeDictionary();
                for (ScriptType o : ((TypeArray) parameters[0]).getObject()) {
                    TypeArray pair = (TypeArray) o;
                    d.getObject().put(pair.getObject().get(0), pair.getObject().get(1));
                }
                return d;
            case 2:
                ScriptType h = parameters[0];
                TypeArray result = new TypeArray();
                if(h instanceof TypeDictionary)
                    for (ScriptType scriptType : ((TypeDictionary)h).getObject().keySet()) {
                        result.getObject().add(scriptType);
                    }
                else if(h instanceof TypeNBTTagCompound){
                    for (String key : ((TypeNBTTagCompound)h).getObject().getKeySet()) {
                        result.getObject().add(new TypeString(key));
                    }
                }
                return result;
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        return false;
    }
}
