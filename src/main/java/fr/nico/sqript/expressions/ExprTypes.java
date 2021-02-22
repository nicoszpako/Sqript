package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;

@Expression(name = "Types Expressions",
        description = "Manipulate the type of a variable",
        examples = "type of \"test\"",
        patterns = {
            "type of {element}:string",
            "{element} parsed as {string}:element",
            "{element} is set:boolean",
            "{element} is not set:boolean",
        },
        priority = -2
)
public class ExprTypes extends ScriptExpression{

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                return new TypeString(ScriptType.getTypeName(parameters[0].getClass()));
            case 1:
                ScriptType parameter_element = parameters[0];
                TypeString parameter_string = (TypeString) parameters[1];
                return (ScriptType) parameter_element.parse(parameter_string.getObject());
            case 2:
                return new TypeBoolean(parameters[0].getObject()!=null);
            case 3:
                return new TypeBoolean(parameters[0].getObject()==null);
            case 4:
                return new TypeBoolean(parameters[0].equals(parameters[1]));
            case 5:
                return new TypeBoolean(!parameters[0].equals(parameters[1]));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
