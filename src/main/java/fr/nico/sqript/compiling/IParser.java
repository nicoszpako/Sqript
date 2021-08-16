package fr.nico.sqript.compiling;

public interface IParser {

    ParseResult parse(ScriptToken line, ScriptCompileGroup group) throws ScriptException;

}
