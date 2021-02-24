package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.expressions.ScriptExpression;

@Action(name = "Print Actions",
        description ="Print something in console",
        examples = {"print \"Hello world !\""
        },
        patterns = {
            "print {element}"
        }
)
public class ActPrint extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                ScriptExpression firstParameter = getParameters().get(0);
                ScriptManager.log.info(line.scriptInstance.getName()+" : "+firstParameter.get(context));
                break;
        }

    }
}
