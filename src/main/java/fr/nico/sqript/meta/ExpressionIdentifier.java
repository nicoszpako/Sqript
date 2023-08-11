package fr.nico.sqript.meta;

import fr.nico.sqript.expressions.ScriptExpression;

import java.util.Objects;

/**
 * Class that uses a priority to compare, and the given expressionClass to create a hash.
 */
public class ExpressionIdentifier implements Comparable{

    private final int priority;
    private final Class<? extends ScriptExpression> expressionClass;

    public ExpressionIdentifier(int priority, Class<? extends ScriptExpression> expressionClass) {
        this.priority = priority;
        this.expressionClass = expressionClass;
    }

    public int getPriority() {
        return priority;
    }

    public Class<? extends ScriptExpression> getExpressionClass() {
        return expressionClass;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionIdentifier that = (ExpressionIdentifier) o;
        return Objects.equals(expressionClass, that.expressionClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressionClass);
    }


    @Override
    public int compareTo(Object o) {
        return ((ExpressionIdentifier) o).priority - getPriority();
    }
}
