package fr.nico.sqript.actions;

import com.google.common.collect.Lists;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptContext;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class ActSimpleExpression extends ScriptAction {

    ScriptExpression exp;

    public ActSimpleExpression(ScriptExpression exp) {
        this.exp = exp;
    }

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        exp.get(context);
    }

    @Override
    public String toString() {
        return "("+exp+")";
    }
}
