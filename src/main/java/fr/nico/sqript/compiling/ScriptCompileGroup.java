package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptAccessor;

import java.util.ArrayList;
import java.util.List;

public class ScriptCompileGroup {

    //Only used for better error catching while compiling script
    //Holds information about current block being compiled, like what variables that can be used, classes, functions etc.

    public List<ScriptAccessor> declaredVariables = new ArrayList<>();
    public List<String> declaredFunctions = new ArrayList<>();

    public ScriptCompileGroup parent;



    public ScriptCompileGroup(){}
    public ScriptCompileGroup(ScriptCompileGroup parent){
        this.parent=parent;
    }

    //Using integer to be able to return "null" reference if variable wasn't found
    public Integer getHashFor(String parameter){
        //System.out.println("Getting hash for : "+parameter);
        for(ScriptAccessor s : declaredVariables){
            //System.out.println("Comparing "+parameter +" with "+s.pattern.pattern());
            if(s.pattern.matcher(parameter).matches()){
                //System.out.println("Matched ! Returning "+s.hash+ " while using + "+s.pattern.pattern()+" : "+s.pattern.pattern().hashCode());
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
