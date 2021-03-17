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

    @Override
    public void build(ScriptLine line, ScriptCompileGroup compileGroup, List<String> parameters, int matchedIndex, int marks) throws Exception {
        //If accessing a global variable,
        //we parse the argument as a string to make the action
        //able to register the new variable in the context
        if (matchedIndex == 2) {
            System.out.println("Building action definition for : "+line);
            String varName = parameters.get(0);
            ScriptExpression to = ScriptDecoder.getExpression(line.with(parameters.get(1)), compileGroup);
            ScriptExpression arg = null;
            if ((arg = ScriptDecoder.getExpression(line.with(varName), compileGroup)) == null && to != null) {
                if (!varName.matches("[^\\s]*")) {
                    ScriptManager.log.error("Variables cannot have whitespaces in their names.");
                    throw new ScriptException.ScriptBadVariableNameException(line);
                }
                compileGroup.add(varName); //We tell the compile group that a variable named parameters.get(0) can now be used in the script
                //System.out.println("Set parameter : "+parameters.get(0));

                ExprReference r = new ExprReference(to.getReturnType());
                r.setVarHash(varName.hashCode());
                r.setLine(line);
                arg = r;
            }
            if (to == null)
                throw new ScriptException.ScriptUnknownExpressionException(line.with(parameters.get(1)));
            this.setParameters(Lists.newArrayList(arg, to));
            this.setMatchedIndex(matchedIndex);
            this.setMarks(marks);
        } else {
            super.build(line, compileGroup, parameters, matchedIndex, marks);
        }

    }
}
