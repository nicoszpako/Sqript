package fr.nico.sqript.compiling;


import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptOperator;

public class OperatorSplitResult {

    private Token[] expressionTokens;
    private Integer[] operatorIndices;

    public OperatorSplitResult(Token[] operands, Integer[] operatorIndices) {
        this.expressionTokens = operands;
        this.operatorIndices = operatorIndices;
    }

    public Token[] getExpressionTokens() {
        return expressionTokens;
    }

    public void setExpressionTokens(Token[] expressionTokens) {
        this.expressionTokens = expressionTokens;
    }

    public Integer[] getOperatorIndices() {
        return operatorIndices;
    }

    public void setOperatorIndices(Integer[] operatorIndices) {
        this.operatorIndices = operatorIndices;
    }




}
