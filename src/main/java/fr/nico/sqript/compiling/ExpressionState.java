package fr.nico.sqript.compiling;

import fr.nico.sqript.compiling.parsers.ScriptExpressionParser;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ExpressionState extends ScriptExpression {

    List<ScriptExpression> branches = new ArrayList<>();

    public ExpressionState(List<ScriptExpression> branches) {
        this.branches = branches;
    }

    public ExpressionState(ScriptExpression expression) {
        this.branches = new ArrayList<>();
        branches.add(expression);
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException, ScriptException.ScriptInterfaceNotImplementedException {
        for(ScriptExpression expression : branches){
            try {
                ScriptType result = expression.get(context);
                return result;
            }catch (Exception e) {
                if(!(e instanceof ClassCastException || e instanceof ScriptException.ScriptTypeException))
                    throw e;
            }
        }
        return null;
    }

    public ScriptType getForType(ScriptContext context, Class[] validTypes) throws ScriptException, ScriptException.ScriptInterfaceNotImplementedException {
        for(ScriptExpression expression : branches){
            if(ScriptExpressionParser.isTypeValid(expression.getReturnType(), validTypes))
                try {
                    ScriptType result = expression.get(context);
                    return result;
                }catch (Exception e) {
                    if(!(e instanceof ClassCastException || e instanceof ScriptException.ScriptTypeException))
                        throw e;
                }
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        for(ScriptExpression expression : branches){
            try {
                return expression.set(context, to);
            }catch (Exception e) {
                if(!(e instanceof ClassCastException || e instanceof ScriptException.ScriptTypeException))
                    throw e;
            }
        }
        return false;
    }

    @Override
    public Class<? extends ScriptElement> getReturnType() {
        return ScriptElement.class;
    }

    public List<ScriptExpression> getBranches() {
        return branches;
    }

    @Override
    public String toString() {
        return "ExpressionState:" +
                "branches=" + branches;
    }

    public void setBranches(List<ScriptExpression> branches) {
        this.branches = branches;
    }
}
