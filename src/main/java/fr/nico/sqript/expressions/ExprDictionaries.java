package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;

@Expression(name = "Dictionaries",
        description = "Utilities about dictionaries",
        examples = "dictionary[\"key\"]",
        patterns = {
                "[a] [new] dictionary:dictionary",
                "[a] [new] dictionary with {array*}:dictionary"
        }
)
public class ExprDictionaries extends ScriptExpression{



    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                return new TypeDictionary();
            case 1:
                TypeDictionary d = new TypeDictionary();
                for(ScriptType o : ((TypeArray)parameters[0]).getObject()){
                    TypeArray pair = (TypeArray)o;
                    d.getObject().put(pair.getObject().get(0),pair.getObject().get(1));
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
