package fr.nico.sqript.function;

import fr.nico.sqript.meta.Native;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;

@Native(
        name="maths",
        definitions = {
        "cos(number):number",
        "sin(number):number",
        "tan(number):number",
        "max(number...):number",
        "sqrt(number):number",
        "floor(number):number",
        "round(number):number",
        "ceil(number):number",
        "abs(number):number"
        },
        description = "Maths things. All angles need to be in radians.",
        examples = {"cos(4)"})
public class FuncMaths extends ScriptNativeFunction {


    public FuncMaths(int matchedDefinition) {
        super(matchedDefinition);
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType... parameters) {
        switch(matchedDefinition){
            case 0:
                return new TypeNumber(Math.cos((Double) parameters[0].getObject()));
            case 1:
                return new TypeNumber(Math.sin((Double) parameters[0].getObject()));
            case 2:
                return new TypeNumber(Math.tan((Double) parameters[0].getObject()));
            case 3:
                TypeNumber best = (TypeNumber) parameters[0];
                for(int i = 1;i<parameters.length;i++){
                    TypeNumber n = (TypeNumber)parameters[i];
                    if(n.getObject()>best.getObject())
                        best=n;
                }
                return best;
            case 4:
                return new TypeNumber(Math.sqrt((Double) parameters[0].getObject()));
            case 5:

                return new TypeNumber((int)Math.floor((Double) parameters[0].getObject()));
            case 6:
                return new TypeNumber((int)Math.round((Double) parameters[0].getObject()));
            case 7:
                return new TypeNumber((int)Math.ceil((Double) parameters[0].getObject()));
            case 8:
                return new TypeNumber(Math.abs((Double) parameters[0].getObject()));
        }
        return null;
    }
}
