package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptAccessor;

import java.util.ArrayList;
import java.util.List;

public class ScriptCompileGroup {

    //Used to determine which accessors can be used in a context.

    public List<ScriptAccessor> declaredVariables = new ArrayList<>();

    public ScriptCompileGroup parent;

    public ScriptCompileGroup(){}
    public ScriptCompileGroup(ScriptCompileGroup parent){
        this.parent=parent;
    }

    //Using integer to be able to return "null" reference if variable wasn't found
    public Integer getHashFor(String parameter){
        //System.out.println("Getting hash for : "+parameter);
        for(ScriptAccessor s : declaredVariables){
            //System.out.println("Comparing "+parameter +" with "+s.key);
            if(s.getPattern().matcher(parameter).matches()){
                //System.out.println("Matched ! Returning "+s.hash+ " while using + "+s.key+" : "+s.getPattern().pattern().hashCode());
                return s.hash;//TODO Dynamic type matching
            }
        }
        if(parent != null)
            return parent.getHashFor(parameter);
        return null;
    }

    public void add(String variable){
        ScriptAccessor sa = new ScriptAccessor(null,variable);
        declaredVariables.add(sa);
    }

    public void add(String variable,int hash){
        ScriptAccessor sa = new ScriptAccessor(null,variable,hash);
        declaredVariables.add(sa);
    }

    public void debugVariables(){
        //System.out.println(this);
        for(ScriptAccessor s : declaredVariables){
            //System.out.println(s.pattern+" : "+s.element);
        }
    }


    public void addArray(List<String> asList) {
        for(String s : asList)
            add(s.split(":",2)[0]);
    }
}
