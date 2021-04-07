package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptInstance;

public class ScriptLine{

    //Holds a full line or a part of a line of a script

    public String text;
    public int number;
    public ScriptInstance scriptInstance;

    public String getText() {
        return text;
    }

    public int getNumber() {
        return number;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public ScriptLine(String text, int number, ScriptInstance scriptInstance) {
        this.text = text;
        this.number = number;
        this.scriptInstance = scriptInstance;
    }

    @Override
    public String toString() {
        return "[line n°"+(number+1)+"] \""+ text+"\"";
    }


    @Override
    public Object clone()  {
        return new ScriptLine(text,number, scriptInstance);
    }

    public ScriptLine with(String parameter) {
        return new ScriptLine(parameter,number, scriptInstance);
    }

    public ScriptLine withString(String[] strings){
        text = ScriptDecoder.replaceStrings(text,strings);
        return this;
    }

}