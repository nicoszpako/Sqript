package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptInstance;

/**
 * Holds a full line or a part of a line of a script
 */
public class ScriptToken {

    /**
     * The raw text representing this token
     */
    private String text;

    /**
     * The line number where this token is.
     */
    private final int lineNumber;

    /**
     * The ScriptInstance containing this ScriptToken.
     */
    private final ScriptInstance scriptInstance;

    public String getText() {
        return text;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public ScriptToken(String text, int number, ScriptInstance scriptInstance) {
        this.text = text;
        this.lineNumber = number;
        this.scriptInstance = scriptInstance;
    }

    @Override
    public String toString() {
        return "["+getScriptInstance().getScriptFile().getName()+" at line n°"+(lineNumber +1)+"] \""+ text+"\"";
    }

    @Override
    public Object clone()  {
        return new ScriptToken(text, lineNumber, scriptInstance);
    }

    public ScriptToken with(String parameter) {
        return new ScriptToken(parameter, lineNumber, scriptInstance);
    }

    public ScriptToken withString(String[] strings){
        text = ScriptDecoder.replaceStrings(text,strings);
        return this;
    }

    public void trim() {
        setText(text.trim());
    }

    public void setText(String text) {
        this.text = text;
    }
}