package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;

@Expression(name = "Dictionaries",
        features = {
                @Feature(name = "Dictionary", description = "Returns a new dictionary.", examples = "set {my_dictionary} to a new dictionary", pattern = "[a] [new] dictionary", type = "dictionary"),
                @Feature(name = "Dictionary from an array of key-value couples", description = "Returns a new dictionary based on elements of an array that represent key-value couples.", examples = "set {my_dictionary} to a new dictionary with [[\"Key 1\",\"Value 1\"]]", pattern = "[a] [new] dictionary with {array*}", type = "dictionary"),
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
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        return false;
    }
}
