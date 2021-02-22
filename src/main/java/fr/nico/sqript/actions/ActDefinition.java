package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;

@Action(name = "Simple Operation Actions",
        description ="Add, remove or set a variable to another",
        examples = "add 1 to A",
        patterns = {"add {element} to {element}",
                "remove {element} to {element}",
                "set {element} to {element}",
        }
)
public class ActDefinition extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        final ScriptExpression second = getParameters().get(1);
        final ScriptExpression first = getParameters().get(0);
        final ScriptType<?> a = first.get(context);
        final ScriptType<?> b = second.get(context);
        switch (getMatchedIndex()){
            case 0:
                if(!second.set(context,ScriptManager.getBinaryOperation(b.getClass(),a.getClass(), ScriptOperator.ADD).operate(b,a))){
                    throw new ScriptException.ScriptNonSettableException(line, first);
                }
                break;
            case 1:
                if(!second.set(context,ScriptManager.getBinaryOperation(b.getClass(),a.getClass(), ScriptOperator.SUBTRACT).operate(b,a))){
                    throw new ScriptException.ScriptNonSettableException(line, first);
                }
                break;
            case 2:
                if(!first.set(context,b)){
                    throw new ScriptException.ScriptNonSettableException(line,first);
                }
                break;
        }
    }

}
