package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public interface IParser {

    ParseResult parse(ScriptLine line, ScriptCompileGroup group) throws ScriptException;

}
