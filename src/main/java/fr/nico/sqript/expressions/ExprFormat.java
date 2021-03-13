package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.interfaces.IFormatable;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Format Expressions",
        description = "Format things",
        examples = "5.643 formatted as \"--,--\"",
        patterns = {
            "{element} formatted as {string}:string"
        }
)
public class ExprFormat extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) throws ScriptException {

        switch(getMatchedIndex()){
            case 0:
                ScriptType element = parameters[0];
                String format = (String) parameters[1].getObject();
                if(!(element instanceof IFormatable)){
                    throw new ScriptException.ScriptInterfaceNotImplementedException(line, IFormatable.class,element.getClass());
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
