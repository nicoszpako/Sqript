package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptElement;

public class NodeSwitch extends Node {

    public NodeSwitch(Node[] children) {
        super(children);
    }

    @Override
    public Class getReturnType() {
        return ScriptElement.class;
    }

    @Override
    public String toString() {
        return "switch:("+super.toString()+")";
    }
}
