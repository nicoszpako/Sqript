package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public interface IParser {

    Node parse(ScriptToken expressionToken, ScriptCompilationContext group, Class[] validTypes) throws ScriptException;

}
