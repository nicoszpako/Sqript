package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.Random;

@Expression(name = "Randomness Expressions",
        features = {
                @Feature(name = "Random number", description = "Returns a random number between two numbers", examples = "random number between 1 and 50", pattern = "random (number|([float] number)|float) [between {number} and {number}]", type = "number"),
                @Feature(name = "Random integer", description = "Returns a random integer between two numbers", examples = "random integer between 1 and 50", pattern = "random (integer|(integer number)|int) between {number} and {number}", type = "number"),
        }
)
public class ExprRandom extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch (getMatchedIndex()) {
            case 0:
                double a = getParameterOrDefault(parameters[0], 0d);
                double b = getParameterOrDefault(parameters[1], 1d);
                Random random = new Random();
                return new TypeNumber(random.nextDouble() * (Math.abs(a - b)) + Math.min(a, b));
            case 1:
                a = getParameterOrDefault(parameters[0], 0d);
                b = getParameterOrDefault(parameters[1], 10d);
                random = new Random();
                return new TypeNumber(Math.round(random.nextDouble() * (Math.abs(a - b)) + Math.min(a, b)));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
