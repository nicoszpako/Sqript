package fr.nico.sqript.actions;

import com.google.common.collect.Lists;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.expressions.ExprReference;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;

import java.util.List;

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
        //System.out.println("Executing line : "+getLine()+" with index:  "+getMatchedIndex());
        switch (getMatchedIndex()){
            case 0:
                if(!second.set(context,ScriptManager.getBinaryOperation(b.getClass(),a.getClass(), ScriptOperator.ADD).operate(b,a))){
                    throw new ScriptException.ScriptNonSettableException(getLine(), first);
                }
                break;
            case 1:
                if(!second.set(context,ScriptManager.getBinaryOperation(b.getClass(),a.getClass(), ScriptOperator.SUBTRACT).operate(b,a))){
                    throw new ScriptException.ScriptNonSettableException(getLine(), first);
                }
                break;
            case 2:
                if(!first.set(context,b)){
                    throw new ScriptException.ScriptNonSettableException(getLine(),first);
                }
                break;
        }
    }

    @Override
    public void build(ScriptLine line, ScriptCompileGroup compileGroup, List<String> parameters, int matchedIndex, int marks) throws Exception {
        //If accessing a global variable,
        //we parse the argument as a string to make the action
        //able to register the new variable in the context
        if (matchedIndex == 2) {
            System.out.println("Set ! : "+line);
            ScriptExpression arg = ScriptDecoder.getExpression(line.with(parameters.get(0)),compileGroup);
            ScriptExpression to = ScriptDecoder.getExpression(line.with(parameters.get(1)),compileGroup);
            if (to == null)
                throw new ScriptException.ScriptUnknownExpressionException(line.with(parameters.get(1)));
            this.setParameters(Lists.newArrayList(arg, to));
            this.setMatchedIndex(matchedIndex);
            this.setMarks(marks);
        } else {
            super.build(getLine(),compileGroup, parameters, matchedIndex, marks);
        }

    }
}
