package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

public abstract class ScriptExpression {

    //Quelque chose qui retourne un ScriptElement.
    //ex : inventory of player -> ExprItemContainer return TypeArray<TypeItem>
    //ex : item in hand of player -> ExprItem return TypeItem
    //ex : 5+9*9 -> ExprCompiledEvaluation return TypeNumber

    //Default implementation of expressions returns null, because return type is get with patterns
    //Only useful with special expressions which are not registered commonly
    public Class<? extends ScriptElement> getReturnType(){
        return null;
    };

    public ScriptType get(ScriptContext context) throws ScriptException {
        return this.get(context, new ScriptType[0]);
    }

    public boolean set(ScriptContext context, ScriptType to) throws ScriptException {
        return this.set(context, to,new ScriptType[0]);
    }

    public abstract ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException, ScriptException.ScriptInterfaceNotImplementedException;

    public abstract boolean set(ScriptContext context,ScriptType to, ScriptType<?>[] parameters) throws ScriptException; //True if it can be set to another value, or false.

    private int matchedIndex;

    int marks;

    ScriptLine line;

    public <T> T getParameterOrDefault(ScriptType<T> parameter, T defaultValue){
        return parameter == null ? defaultValue : parameter.getObject();
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getMatchedIndex() {
        return matchedIndex;
    }

    public ScriptLine getLine() {
        return line;
    }

    public void setMatchedIndex(int matchedIndex) {
        this.matchedIndex = matchedIndex;
    }

    public void setLine(ScriptLine line) {
        this.line = line;
    }
}
