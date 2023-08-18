package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.interfaces.IFormatable;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;

@Expression(name = "Number Expressions",
        features = {
                @Feature(name = "Number is between", description = "Returns true if the given number is between the given bounds (inclusively).", examples = "{number} is between 5 and 15", pattern = "{number} is between {number} and {number}", type = "boolean")
        }
)
public class ExprNumbers extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {

        switch (getMatchedIndex()) {
            case 0:
                double x = (double)parameters[0].getObject();
                double a = (double)parameters[1].getObject();
                double b = (double)parameters[2].getObject();
                return new TypeBoolean(x<=b && x>=a);

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
