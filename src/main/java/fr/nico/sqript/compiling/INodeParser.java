package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public interface INodeParser {

    Node parse(ScriptToken expressionToken, ScriptCompilationContext group, Class[] validTypes) throws ScriptException;

}
