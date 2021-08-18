package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.types.ScriptType;

import java.util.*;
import java.util.regex.Matcher;

/**
 * The ScriptContext is what contains the variables / elements common to different blocks of the script.
 * It is distributed recursively to each sub-block, in particular so that a block can take advantage of the variables declared in the block which is superior to it.
 * A ScriptContext comes down to a list of variables: the ScriptAccessor (accessor because they can be accessed from the script via a regex).
 * Each ScriptEvent must be able to provide an initial ScriptContext during its construction, even empty.
 */
public class ScriptContext {

    /**
     * HashMap associating a ScriptAccessor to each variable hash
     */
    private final HashMap<Integer, ScriptTypeAccessor> variables = new HashMap<>();

    /**
     * The parent of this ScriptContext.
     */
    private ScriptContext parent;

    /**
     * Removes the variable associated to the given name from this ScriptContext.
     *
     * @param name The variable name.
     */
    public void remove(String name) {
        ScriptTypeAccessor a = getAccessor(name);
        variables.remove(a.hash);
    }

    /**
     * Removes the variable associated to the given hash from this ScriptContext.
     *
     * @param hash The variable hash.
     */
    public void remove(int hash) {
        variables.remove(hash);
    }

    /**
     * A non-null accessor means that a returnValue is wanted here.
     * If a child context has this accessor, to null, it will try to give the return to his parent, and recursively.
     */
    private ScriptTypeAccessor returnValue = null;

    /**
     * Utility method which creates a ScriptContext extending the global one
     *
     * @return A ScriptContext extending the global one
     */
    public static ScriptContext fromGlobal() {
        return new ScriptContext(ScriptManager.GLOBAL_CONTEXT);
    }

    public ScriptContext() {
    }

    public ScriptContext(ScriptContext parent) {
        this.parent = parent;
    }


    /**
     * Propagates the return value of this ScriptContext to its parent.
     */
    public void setReturnValue(ScriptType returnValue) {
        if (this.returnValue != null)
            this.returnValue.element = returnValue;
        else if (parent != null)
            parent.setReturnValue(returnValue);
    }

    /**
     * Debug purpose only.
     *
     * @return a String holding information about this ScriptContext.
     */
    public String printVariables() {
        String s = "(" + this.hashCode() + ") ";
        for (int a : variables.keySet()) {
            s += "[" + variables.get(a).key + "/" + a + ":" + variables.get(a).element + "] ";
        }
        if (parent != null)
            s += " => [" + parent.printVariables() + "]";
        return s;
    }

    /**
     * Returns the variable associated to the given hash.
     *
     * @param hash The hash of the variable.
     * @return the variable associated to the given hash.
     */
    public ScriptType<?> getVariable(int hash) {
        //System.out.println("Getting variable for : "+hash+" in "+ this.hashCode());
        //System.out.println("It contains : "+printVariables());
        final ScriptTypeAccessor a = variables.get(hash);
        if (a != null)
            return a.element;
        else if (parent != null)
            return parent.getVariable(hash);
        else return null;
    }

    public ScriptType<?> getVariable(String name) {
        return getVariable(getHash(name));
    }

    /**
     * Returns the hash associated to the given variable name.
     *
     * @param variableName The variable's name.
     * @return The hash associated to the given name.
     */
    public int getHash(String variableName) {
        //System.out.println("Getting hash for : "+variableName+" in "+ this.hashCode());
        for (ScriptTypeAccessor a : variables.values()) {//Simple search first
            if (a.key != null && a.key.equals(variableName))
                return a.hash;
        }
        for (Integer hash : variables.keySet()) {//Pattern search second
            if (variables.get(hash).getPattern() != null) {
                Matcher m = variables.get(hash).getPattern().matcher(variableName);
                //System.out.println("check if "+variables.get(hash).pattern.pattern()+" matches "+variableName+" it's : "+m.matches());
                if (m.matches())
                    return hash;
            }

        }
        if (parent != null) {
            return parent.getHash(variableName);
        }
        return variableName.hashCode();
    }

    /**
     * Returns the list of the inner variables of this context (those not declared in the script, but by the event or by other blocks).
     *
     * @return The list of this ScriptContext's accessors.
     */
    public Collection<ScriptTypeAccessor> getAccessors() {
        return variables.values();
    }

    /**
     * Returns the TypeAccessor associated to the given string token.
     *
     * @param token The string token to parse into a ScriptTypeAccessor.
     * @return the ScriptTypeAccessor matching the given string token.
     */
    public ScriptTypeAccessor getAccessor(String token) {
        for (ScriptTypeAccessor a : variables.values()) {
            if (a.getPattern() == null)
                continue;
            if (token.equals(a.key) || a.getPattern().matcher(token).matches()) return a;
        }
        if (parent != null)
            return parent.getAccessor(token);
        ScriptTypeAccessor typeAccessor = new ScriptTypeAccessor(null, token);
        put(typeAccessor);
        return typeAccessor;
    }

    public void put(ScriptTypeAccessor accessor) {
        variables.put(accessor.hash, accessor);
    }

    /**
     * Wraps the given ScriptTypeAccessor array into this ScriptContext.
     *
     * @param accessors The ScriptTypeAccessor array that will be wrapped.
     * @return the instance of this ScriptContext once the array is wrapped.
     */
    public ScriptContext wrap(ScriptTypeAccessor... accessors) {
        for (ScriptTypeAccessor accessor : accessors) {
            variables.put(accessor.hash, accessor);
        }
        return this;
    }

    /**
     * Clears this ScriptContext's variables array.
     */
    public void empty() {
        this.variables.clear();
    }

    public ScriptTypeAccessor getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ScriptTypeAccessor returnValue) {
        this.returnValue = returnValue;
    }

    public ScriptTypeAccessor getAccessor(Integer varHash) {
        return variables.get(varHash);
    }
}
