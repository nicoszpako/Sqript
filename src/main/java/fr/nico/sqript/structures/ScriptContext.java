package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.types.ScriptType;
import sun.font.Script;

import java.util.*;
import java.util.regex.Matcher;

public class ScriptContext {

    /*
    Le ScriptContext est ce qui contient les variables/élements communs à différents blocs du scripts.
    Il est distribué récursivement à chaque sous-bloc, notamment pour qu'un bloc puisse profiter des variables déclarées dans le bloc qui lui est supérieur.
    Un ScriptContext se résume à une liste de variables : les ScriptAccessor (accessor car on peut les accéder depuis le script via une regex).
    Chaque ScriptEvent doit être capable de fournir un ScriptContext initial lors de sa construction, même vide.
     */

    //HashMap associating a ScriptAccessor for each hash
    private final HashMap<Integer,ScriptAccessor> variables = new HashMap<>();

    public ScriptContext parent;

    public void remove(String name){
        ScriptAccessor a = getAccessor(name);
        variables.remove(a);
    }

    public void remove(int hash){
        variables.remove(hash);
    }

    //A not-null accessor means that a returnValue is wanted here.
    //If a child context has this accessor, to null, it will try to give the return to his parent, and recursively.
    public ScriptAccessor returnValue = null;

    /**
     * Utility method which creates a ScriptContext extending the global one
     * @return A ScriptContext extending the global one
     */
    public static ScriptContext fromGlobal(){
        return new ScriptContext(ScriptManager.GLOBAL_CONTEXT);
    }

    public ScriptContext(){}

    public ScriptContext(ScriptContext parent){
        this.parent=parent;
    }


    //Return value propagation
    public void setReturnValue(ScriptType returnValue){
        if(this.returnValue!=null)
            this.returnValue.element=returnValue;
        else if(parent!=null)
            parent.setReturnValue(returnValue);
    }

    public String printVariables(){
        String s = "("+this.hashCode()+") ";

        for(int a : variables.keySet()){
            s+=a +": "+ variables.get(a).pattern +":"+variables.get(a).element+" ";
        }
        if(parent!=null)
            s+=" => ["+parent.printVariables()+"]";
        return s;
    }

    //We use hashes of strings to find the right variable stored in the con
    public ScriptType<?> get(int hash){
        //System.out.println("Getting for : "+hash+" in "+ this.hashCode());
        //System.out.println("It contains : "+printVariables());
        final ScriptAccessor a = variables.get(hash);
        if(a != null)
            return a.element;
        else if(parent!=null)
            return parent.get(hash);
        else return null;
    }

    public int get(String match){
        //System.out.println("Getting for : "+match+" in "+ this.hashCode());
        //System.out.println("It contains : "+printVariables());
        int i = 0;
        for (ScriptAccessor a : variables.values()) {//Simple search first
            if(a.pattern.equals(match))return i;
            i++;
        }
        i=0;
        for (ScriptAccessor a : variables.values()) {//Pattern search second
            //System.out.println("check if "+a.pattern.pattern()+" matches "+match);
            Matcher m = a.pattern.matcher(match);
            if(m.matches())return i;
            i++;
        }
        if(parent!=null)
        {
            return parent.get(match);
        }
        return -1;
    }

    public Collection<ScriptAccessor> getAccessors(){
        return variables.values();
    }

    public ScriptAccessor getAccessor(String match){
        for (ScriptAccessor a : variables.values()) {
            //System.out.println("check if "+a.pattern.pattern()+" matches "+match);

            if(match.equals(a.pattern.pattern()) || a.pattern.matcher(match).matches())return a;
        }
        if(parent!=null)
            return parent.getAccessor(match);
        return null;
    }

    public ScriptAccessor getAccessor(int hash){
        final ScriptAccessor a = variables.get(hash);
        if(a != null)
            return a;
        else if(parent!=null)
            return parent.getAccessor(hash);
        else return null;
    }

    public void put(ScriptAccessor accessor){
        variables.put(accessor.hash,accessor);
    }

    public ScriptContext wrap(ScriptAccessor... accessors){
        for(ScriptAccessor accessor : accessors){
            variables.put(accessor.hash,accessor);
        }
        return this;
    }

    public void empty(){
        this.variables.clear();
    }

}
