package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.expressions.ScriptExpression;

@Action(name = "Print Actions",
        features = @Feature(name = "Print", description = "Prints something in the console.", examples = "print \"Hello world !\"", pattern = "print {element}")
)
public class ActPrint extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                ScriptExpression firstParameter = getParameters().get(0);
                ScriptManager.log.info(getLine().getScriptInstance().getName()+" : "+firstParameter.get(context));
                break;
        }
    }
}
