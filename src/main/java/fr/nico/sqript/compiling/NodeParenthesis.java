package fr.nico.sqript.compiling;

public class NodeParenthesis extends Node{

    EnumTokenType type;

    public NodeParenthesis(EnumTokenType type) {
        this.type = type;
    }

    public EnumTokenType getType() {
        return type;
    }

    public void setType(EnumTokenType type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return type.toString()+super.toString();
    }

    @Override
    public Class getReturnType() {
        return null;
    }
}
