package fr.nico.sqript.compiling;


import fr.nico.sqript.structures.ScriptElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Node {

    private Node[] children;

    private ScriptToken line;

    public Node() {}

    public Node(Node[] children) {
        this.children = children;
    }

    public Node(Node[] children, ScriptToken line) {
        this.children = children;
        this.line = line;
    }

    public ScriptToken getLine() {
        return line;
    }

    public void setLine(ScriptToken line) {
        this.line = line;
    }

    public void addChild(Node node){
        List<Node> nodesArray = getChildren() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(getChildren()));
        //System.out.println(tree == null);
        nodesArray.add(node);
        setChildren(nodesArray.toArray(new Node[0]));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(children!=null) {
            builder.append("[");
            for (Node child : children) {
                if(child != null)
                    builder.append(child);
                else builder.append("null");
                builder.append(", ");
            }
            if(builder.length()>2){
                builder.deleteCharAt(builder.length()-1);
                builder.deleteCharAt(builder.length()-1);
            }
            builder.append("]");
        }
        return builder.toString();
    }

    public Node[] getChildren() {
        return children;
    }

    public void setChildren(Node[] children) {
        this.children = children;
    }

    public abstract Class getReturnType();
}
