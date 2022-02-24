package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptElement;

public class NodeExpression extends Node {

    ScriptExpression expression;

    public NodeExpression(Node[] children, ScriptExpression expression) {
        super(children);
        this.expression = expression;
    }

    public NodeExpression(ScriptExpression expression) {
        this.expression = expression;
    }

    public ScriptExpression getExpression() {
        return expression;
    }

    public void setExpression(ScriptExpression expression) {
        this.expression = expression;
    }

    public int getArity(){
        if(getChildren() != null)
            return getChildren().length;
        else return 0;
    }

    @Override
    public String toString() {
        return expression.toString()+super.toString();
    }

    @Override
    public Class getReturnType() {
        //System.out.println("Returning type : "+expression.getReturnType()+" for "+expression.toString());
        return expression.getReturnType();
    }

    public boolean childrenAreNull() {
        for(Node n : getChildren())
            if (n != null) return false;
        return true;
    }
}
