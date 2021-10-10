package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;

import java.util.Arrays;

public class NodeOperation extends Node {

    ScriptOperator operator;

    public NodeOperation(Node[] children, ScriptOperator operator) {
        super(children);
        this.operator = operator;
    }

    public NodeOperation(ScriptOperator operator) {
        this.operator = operator;
    }

    public ScriptOperator getOperator() {
        return operator;
    }

    public void setOperator(ScriptOperator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator.toString()+super.toString();
    }

    @Override
    public Class<? extends ScriptElement> getReturnType() {
        //System.out.println("Getting type for : "+toString());
        if(getChildren().length == 1){
            return ScriptManager.getUnaryOperation(getChildren()[0].getReturnType(), operator).getReturnType();
        }else if(getChildren().length == 2){
            //System.out.println(getChildren()[0] == null);
            //System.out.println(getChildren()[1] == null);
            //System.out.println("Children : "+ Arrays.toString(getChildren()));
            return ScriptManager.getBinaryOperation(getChildren()[0].getReturnType(), getChildren()[1].getReturnType(), operator).getReturnType();
        }
        return null;
    }
}
