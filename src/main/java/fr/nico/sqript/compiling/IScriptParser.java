package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.IScript;

public interface IScriptParser {

    /**
     * Return an IScript from a ScriptLine and a ScriptCompileGroup.
     * Should return null if the line is not matching this parser.
     * @param line The ScriptToken associated to the line to parse.
     * @param compileGroup The CompileGroup to use during parsing.
     * @return the parsed IScript.
     */
    IScript parse(ScriptToken line, ScriptCompilationContext compileGroup);

}
