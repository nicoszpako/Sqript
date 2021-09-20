package fr.nico.sqript.compiling;

import fr.nico.sqript.structures.ScriptOperator;

public class ExtractedOperatorsResult {

    String transformedString;
    ScriptOperator[] operators;

    public ExtractedOperatorsResult(String transformedString, ScriptOperator[] operators) {
        this.transformedString = transformedString;
        this.operators = operators;
    }

    public String getTransformedString() {
        return transformedString;
    }

    public void setTransformedString(String transformedString) {
        this.transformedString = transformedString;
    }

    public ScriptOperator[] getOperators() {
        return operators;
    }

    public void setOperators(ScriptOperator[] operators) {
        this.operators = operators;
    }
}
