package fr.nico.sqript.compiling;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptCompilationContext {

    //Used to determine which accessors can be used in a context.

    public List<ScriptTypeAccessor> declaredVariables = new ArrayList<>();

    public ScriptCompilationContext parent;

    public ScriptCompilationContext(ScriptTypeAccessor... declaredVariables){
        this.declaredVariables.addAll(Arrays.asList(declaredVariables));
    }

    public ScriptCompilationContext(){}
    public ScriptCompilationContext(ScriptCompilationContext parent){
        this.parent=parent;
    }

    //Using integer to be able to return "null" reference if variable wasn't found
    public ScriptTypeAccessor getAccessorFor(String parameter){
        //System.out.println("Getting hash for : "+parameter);
        for(ScriptTypeAccessor s : declaredVariables){
            //System.out.println("Comparing "+parameter +" with "+s.key+" with pattern : "+s.getPattern().pattern());
            if(s.getPattern().matcher(parameter).matches()){
                //System.out.println("Matched ! Returning "+s.hash+ " while using + "+s.key+" : "+s.getPattern().pattern().hashCode());
                return s;//TODO Dynamic type matching
            }
        }
        if(parent != null)
            return parent.getAccessorFor(parameter);
        return null;
    }

    public void add(String variable, Class returnType){
        //System.out.println("Adding variable to compilation context : "+variable+" returning "+returnType);
        ScriptTypeAccessor sa = new ScriptTypeAccessor(null,variable);
        sa.setReturnType(returnType);
        declaredVariables.add(sa);
    }

    public void add(String variable,int hash){
        ScriptTypeAccessor sa = new ScriptTypeAccessor(null,variable,hash);
        declaredVariables.add(sa);
    }

    public void debugVariables(){
        //System.out.println(this);
        for(ScriptTypeAccessor s : declaredVariables){
            //System.out.println(s.pattern+" : "+s.element);
        }
    }


    @Override
    public String toString() {
        return "ScriptCompilationContext{" +
                "declaredVariables=" + declaredVariables + (parent == null ? "" : "["+parent.toString()+"]")+"}";
    }

    public void addArray(List<Feature> asList) {
        for(Feature s : asList)
            add(s.pattern(), ScriptDecoder.parseType(s.type()));
    }
}
