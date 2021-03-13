package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Random;

@Expression(name = "Randomness Expressions",
        description = "Manipulate random things",
        examples = "random integer between 0 and 10",
        patterns = {
            "random (number|([float] number)|float) [between {number} and {number}]:number",
            "random (integer|(integer number)|int) between {number} and {number}:number",
        }
)
public class ExprRandom extends ScriptExpression {


    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                double a = getParameterOrDefault(parameters[0], 0d);
                double b = getParameterOrDefault(parameters[1], 1d);
                Random random = new Random();
                return new TypeNumber(random.nextDouble()*(Math.abs(a-b))+Math.min(a,b));
            case 1:
                a = getParameterOrDefault(parameters[0],0d);
                b = getParameterOrDefault(parameters[1],10d);
                random = new Random();
                return new TypeNumber(Math.round(random.nextDouble()*(Math.abs(a-b))+Math.min(a,b)));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
