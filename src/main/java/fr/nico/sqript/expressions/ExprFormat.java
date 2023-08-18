package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.interfaces.IFormatable;
import fr.nico.sqript.types.primitive.TypeString;

@Expression(name = "Format Expressions",
        features = {
                @Feature(name = "Format", description = "Format things.", examples = "5.643 formatted as \"--,--\"", pattern = "{element} formatted as {string}", type = "string")
        }
)
public class ExprFormat extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {

        switch (getMatchedIndex()) {
            case 0:
                //System.out.println("Formatting");
                ScriptType element = parameters[0];
                String format = (String) parameters[1].getObject();
                if (!(element instanceof IFormatable)) {
                    throw new ScriptException.ScriptInterfaceNotImplementedException(line, IFormatable.class, element.getClass());
                }
                IFormatable formatable = (IFormatable) element;
                return new TypeString(formatable.format(format));

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
