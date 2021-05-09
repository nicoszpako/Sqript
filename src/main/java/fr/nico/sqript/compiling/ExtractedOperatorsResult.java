package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptOperator;

public class ExtractedOperatorsResult {

    String transformedString;
    ScriptOperator[] operators;

    public ExtractedOperatorsResult(String transformedString, ScriptOperator[] operators) {
        this.transformedString = transformedString;
        this.operators = operators;
    }
}
