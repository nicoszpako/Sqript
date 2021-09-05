package fr.nico.sqript.compiling;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionTree {

    private ExpressionTree[] leaves;

    ScriptExpression expression;
    ScriptOperator operator;
    EnumTokenType type;

    public ExpressionTree() {
    }

    public ExpressionTree(EnumTokenType mode) {
        this.type = mode;
    }

    public ExpressionTree(ScriptExpression expression) {
        this(expression, null);
    }

    public ExpressionTree(ScriptExpression expression, ExpressionTree[] leaves) {
        this.expression = expression;
        this.leaves = leaves;
        this.type = EnumTokenType.EXPRESSION;
    }

    public ExpressionTree(ScriptOperator operator) {
        this(operator, null);
    }

    public ExpressionTree(ScriptOperator operator, ExpressionTree[] leaves) {
        this.operator = operator;
        this.leaves = leaves;
        this.type = EnumTokenType.OPERATOR;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (type){
            case EXPRESSION:
                builder.append("[").append(expression == null ? "null" : expression.toString()).append("]");
                break;
            case OPERATOR:
                builder.append("[").append(operator.toString()).append("]");
                break;
        }
        if(leaves!=null){
            builder.append(" [");
            for(ExpressionTree leaf : leaves){
                builder.append(leaf.toString()).append(",");
            }
            builder.append("]");
        }

        return builder.toString();
    }

    public Class getReturnType(){
        if(getType() == EnumTokenType.EXPRESSION){
            return expression.getReturnType();
        }else if(getType() == EnumTokenType.OPERATOR){
            return ScriptManager.getBinaryOperation(getLeaves()[0].getReturnType(), getLeaves()[1].getReturnType(), getOperator()).getReturnType();
        }
        return null;
    }

    public void addLeave(ExpressionTree tree){
        List<ExpressionTree> leavesArray = getLeaves() == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(getLeaves()));
        System.out.println(tree == null);
        leavesArray.add(tree);
        setLeaves(leavesArray.toArray(new ExpressionTree[0]));
    }

    public ScriptExpression getExpression() {
        return expression;
    }

    public void setExpression(ScriptExpression expression) {
        this.expression = expression;
    }

    public ScriptOperator getOperator() {
        return operator;
    }

    public void setOperator(ScriptOperator operator) {
        this.operator = operator;
    }

    public ExpressionTree[] getLeaves() {
        return leaves;
    }

    public int getLeavesCount(){
        return leaves == null ? 0 : leaves.length;
    }

    public void setLeaves(ExpressionTree[] leaves) {
        this.leaves = leaves;
    }

    public EnumTokenType getType() {
        return type;
    }

    public void setType(EnumTokenType type) {
        this.type = type;
    }


}
