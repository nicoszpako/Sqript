package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptParameterDefinition;

public interface IParser {

    ExpressionTree parse(ScriptToken expressionToken, ScriptCompilationContext group) throws ScriptException;

}
