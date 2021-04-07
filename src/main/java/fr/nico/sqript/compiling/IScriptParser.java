package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.IScript;

public interface IScriptParser {

    /**
     * Return an IScript from a ScriptLine and a ScriptCompileGroup.
     * Should return null if the line is not matching this parser.
     * @param line
     * @param compileGroup
     * @return the IScript
     */
    public IScript parse(ScriptLine line, ScriptCompileGroup compileGroup);

}
