package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;

public abstract class IScript {

    /**
     * Abstract method that describes the behavior of this Script
     * @param context The ScriptContext that the execution must use.
     * @throws ScriptException if an error happens during execution.
     */
    public abstract void execute(ScriptContext context) throws ScriptException;

    /**
     * The next <strong>direct</strong> script item to be run
     */
    public IScript next;

    public void setNext(IScript next) {
        this.next = next;
    }

    public IScript getNext(ScriptContext context) throws ScriptException {
        if(next!=null)
            return next;
        else if(getParent()!=null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        }else
            return null;
    }

    private ScriptToken line;

    public ScriptToken getLine() {
        return line;
    }

    public void setLine(ScriptToken line) {
        this.line = line;
    }

    /**
     * The next <strong>direct</strong> parent of this script item
     */
    public IScript parent;

    public void setParent(IScript parent) {
        this.parent = parent;
    }

    public IScript getParent() {
        return parent;
    }

    /**
     * Should not be overridden for normal use.
     * @param context The context the running must use.
     * @return The next IScript to run
     * @throws ScriptException if an error happens during run.
     */
    public IScript run(ScriptContext context) throws ScriptException {
        execute(context);
        return getNext(context);
    }


}
