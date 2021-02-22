package fr.nico.sqript.structures;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;

public abstract class IScript {

    /**
     * Abstract method that describes the behavior of this Script
     * @param context
     * @throws ScriptException
     */
    public abstract void execute(ScriptContext context) throws ScriptException;

    /**
     * The next <strong>direct</strong> script item to be run
     */
    public IScript next;

    public void setNext(IScript next) {
        this.next = next;
    }

    public IScript getNext(ScriptContext context) {
        if(next!=null)
            return next;
        else if(getParent()!=null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        }else
            return null;
    }




    private ScriptLine line;

    public ScriptLine getLine() {
        return line;
    }

    public void setLine(ScriptLine line) {
        this.line = line;
    }

    public int hash;

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
     * Should not be overriden for normal use.
     * @param context
     * @return The the next IScript to run
     * @throws ScriptException
     */
    public IScript run(ScriptContext context) throws ScriptException {
        execute(context);
        return getNext(context);
    }


}
