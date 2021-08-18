package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;

@Expression(name = "Types Expressions",
        features = {
            @Feature(name = "Type of element", description = "Returns the type of an element.", examples = "type of {my_variable}", pattern = "type of {element}", type = "string"),
            @Feature(name = "Element parsed as another type", description = "Returns the element parsed as another given type.", examples = "\"5\" parsed as \"number\" #Returns 5", pattern = "{element} parsed as {string}"),
            @Feature(name = "Element is set", description = "Returns whether a given element is defined (not null).", examples = "{my_variable} is defined", pattern = "{element} is (set|defined)", type = "boolean"),
            @Feature(name = "Element is not set", description = "Returns whether a given element is not defined (null).", examples = "{my_variable} is not defined", pattern = "{element} is not (set|defined)", type = "boolean"),
            @Feature(name = "Element is not element (is not equal to)", description = "Returns whether a given element is not equal to another one.", examples = "{my_variable} is not \"Test\"", pattern = "{element} is not {element}", type = "boolean"),
            @Feature(name = "Element is element (is equal to)", description = "Returns whether a given element is equal to another one.", examples = "{my_variable} is \"Test\"", pattern = "{element} is {element}", type = "boolean"),
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
                return new TypeBoolean(!(parameters[0] instanceof TypeNull) && parameters[0].getObject()!=null) ;
            case 3:
                return new TypeBoolean((parameters[0] instanceof TypeNull) || parameters[0].getObject()==null );
            case 4:
                return new TypeBoolean(!parameters[0].equals(parameters[1]));
            case 5:
                return new TypeBoolean(parameters[0].equals(parameters[1]));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
