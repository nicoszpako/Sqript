package fr.nico.sqript.actions;

import com.google.common.collect.Lists;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.expressions.ExprCompiledExpression;
import fr.nico.sqript.expressions.ExprReference;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Action(name = "Simple Operation Actions",
        features = {
                @Feature(name = "Add an element to another", description = "Adds an element to another elements if the + operation can be applied on it.", examples = {"add \"d\" to [\"a\",\"b\",\"c\"]", "add 1 to {counter}"}, pattern = "add {element} to {element}"),
                @Feature(name = "Remove an element from another", description = "Removes an element from another element if the - operation can be applied on it.", examples = {"remove \"c\" from [\"a\",\"b\",\"c\"]", "remove 1 from player's health"}, pattern = "remove {element} from {element}"),
                @Feature(name = "Set an element to another", description = "Defines the value of an element.", examples = {"set player's health to 20", "set {variable} to 8"}, pattern = "set {element} to {element}")
        },
        priority = -1
)

public class ActDefinition extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        final ScriptExpression second = getParameters().get(1);
        final ScriptExpression first = getParameters().get(0);
        final ScriptType<?> a = first.get(context);
        final ScriptType<?> b = second.get(context);
        //System.out.println("Executing line : "+getLine()+" with index:  "+getMatchedIndex());
        //System.out.println("a: "+a);

        //System.out.println("b: "+b);
        switch (getMatchedIndex()) {
            case 0:
                if (!second.set(context, ScriptManager.getBinaryOperation(b.getClass(), a.getClass(), ScriptOperator.ADD).getOperation().operate(b, a))) {
                    throw new ScriptException.ScriptNonSettableException(getLine(), first);
                }
                break;
            case 1:
                if (!second.set(context, ScriptManager.getBinaryOperation(b.getClass(), a.getClass(), ScriptOperator.SUBTRACT).getOperation().operate(b, a))) {
                    throw new ScriptException.ScriptNonSettableException(getLine(), first);
                }
                break;
            case 2:
                if (!first.set(context, b)) {
                    throw new ScriptException.ScriptNonSettableException(getLine(), first);
                }
                break;
        }
    }

    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup, List<String> parameters, int matchedIndex, int marks) throws Exception {
        //If accessing a global variable,
        //we parse the argument as a string to make the action
        //able to register the new variable in the context
        if (matchedIndex == 2) {
            //System.out.println("Set ! : "+line);

            ScriptExpression arg = ScriptDecoder.parse(line.with(parameters.get(0)), compileGroup);

            /**
             * If a state, we only choose the expressions that can be set for performance issues.
             */
            if (arg instanceof ExprCompiledExpression) {
                ExprCompiledExpression state = (ExprCompiledExpression) arg;
                if (state.ast instanceof NodeSwitch) {
                    ArrayList<Node> branches = new ArrayList<>(Arrays.asList(state.ast.getChildren()));
                    branches.removeIf(b -> !(b instanceof NodeExpression) || ((NodeExpression) b).getExpression() == null);
                    branches.removeIf(b -> ScriptManager.getDefinitionFromExpression(((NodeExpression) b).getExpression().getClass()) != null && !ScriptManager.getDefinitionFromExpression(((NodeExpression) b).getExpression().getClass()).getFeatures()[((NodeExpression) b).getExpression().getMatchedIndex()].settable());
                    if (branches.size() == 1)
                        arg = new ExprCompiledExpression(branches.get(0));
                    else state.ast.setChildren(branches.toArray(new Node[0]));
                }
            }

            ScriptExpression to = ScriptDecoder.parse(line.with(parameters.get(1)), compileGroup,new Class[]{arg.getReturnType()});
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
