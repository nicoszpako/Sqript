package fr.nico.sqript.compiling;


public class OperatorSplitResult {

    private ExpressionToken[] expressionTokens;
    private Integer[] operatorIndices;

    public OperatorSplitResult(ExpressionToken[] operands, Integer[] operatorIndices) {
        this.expressionTokens = operands;
        this.operatorIndices = operatorIndices;
    }

    public ExpressionToken[] getExpressionTokens() {
        return expressionTokens;
    }

    public void setExpressionTokens(ExpressionToken[] expressionTokens) {
        this.expressionTokens = expressionTokens;
    }

    public Integer[] getOperatorIndices() {
        return operatorIndices;
    }

    public void setOperatorIndices(Integer[] operatorIndices) {
        this.operatorIndices = operatorIndices;
    }




}
