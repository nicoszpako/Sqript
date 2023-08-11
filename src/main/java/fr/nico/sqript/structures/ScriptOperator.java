package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import net.minecraft.command.CommandBase;

import java.util.Objects;

public class ScriptOperator {

    public static final ScriptOperator PLUS_UNARY = new ScriptOperator("+", 14, Associativity.RIGHT_TO_LEFT, true);//Unary +
    public static final ScriptOperator FACTORIAL = new ScriptOperator("!", 14, Associativity.RIGHT_TO_LEFT, true, false, true);//Factorial
    public static final ScriptOperator MINUS_UNARY = new ScriptOperator("-", 14, Associativity.RIGHT_TO_LEFT, true);//Unary -
    public static final ScriptOperator EXP = new ScriptOperator("^", 13, Associativity.LEFT_TO_RIGHT);//Multiplication
    public static final ScriptOperator MULTIPLY = new ScriptOperator("*", 12, Associativity.LEFT_TO_RIGHT);//Multiplication
    public static final ScriptOperator MOD = new ScriptOperator("%", 12, Associativity.LEFT_TO_RIGHT);//Multiplication
    public static final ScriptOperator QUOTIENT = new ScriptOperator("//", 12, Associativity.LEFT_TO_RIGHT);//Division
    public static final ScriptOperator DIVIDE = new ScriptOperator("/", 12, Associativity.LEFT_TO_RIGHT);//Division
    public static final ScriptOperator ADD = new ScriptOperator("+", 11, Associativity.LEFT_TO_RIGHT);//Addition
    public static final ScriptOperator SUBTRACT = new ScriptOperator("-", 11, Associativity.LEFT_TO_RIGHT);//Subtraction
    public static final ScriptOperator MTE = new ScriptOperator(">=", 9, Associativity.LEFT_TO_RIGHT);//More than or equal to
    public static final ScriptOperator LTE = new ScriptOperator("<=", 9, Associativity.LEFT_TO_RIGHT);//Less than or equal to
    public static final ScriptOperator LT = new ScriptOperator("<", 9, Associativity.LEFT_TO_RIGHT);//Less than
    public static final ScriptOperator MT = new ScriptOperator(">", 9, Associativity.LEFT_TO_RIGHT);//More than
    public static final ScriptOperator EQUAL = new ScriptOperator("=", 8, Associativity.LEFT_TO_RIGHT);//Equality
    public static final ScriptOperator NOT_EQUAL = new ScriptOperator("!=", 8, Associativity.LEFT_TO_RIGHT);//Inequality
    public static final ScriptOperator NOT = new ScriptOperator("not", 5, Associativity.LEFT_TO_RIGHT, true, true);//Unary logical NOT (not true == false)
    public static final ScriptOperator AND = new ScriptOperator("and", 4, Associativity.LEFT_TO_RIGHT, false, true);//Logical AND
    public static final ScriptOperator OR = new ScriptOperator("or", 3, Associativity.LEFT_TO_RIGHT, false, true);//Logical OR

    static {
        ScriptManager.registerBinaryOperation(NOT_EQUAL, ScriptType.class, ScriptType.class, TypeBoolean.class, (a, b) ->
                new TypeBoolean(!((TypeBoolean) ScriptManager.getBinaryOperation(a.getClass(), b.getClass(), EQUAL).getOperation().operate(a, b)).getObject()));
    }

    public ScriptOperator(String symbol, int priority, Associativity associativity, boolean unary, boolean word, boolean postfixed) {
        this.symbol = symbol;
        this.priority = (byte) priority;
        this.associativity = associativity;
        this.unary = unary;
        this.word = word;
        this.postfixed = postfixed;
        hashCode = Objects.hash(associativity, symbol, priority, unary, word, postfixed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptOperator that = (ScriptOperator) o;
        return priority == that.priority &&
                unary == that.unary &&
                word == that.word &&
                postfixed == that.postfixed &&
                associativity == that.associativity &&
                symbol.equals(that.symbol);
    }


    private final int hashCode;

    @Override
    public int hashCode() {
        return hashCode;
    }

    public ScriptOperator(String symbol, int priority, Associativity associativity, boolean unary, boolean word) {
        this(symbol, priority, associativity, unary, word, false);
    }

    public ScriptOperator(String symbol, int priority, Associativity associativity) {
        this(symbol, priority, associativity, false);
    }

    public ScriptOperator(String symbol, int priority, Associativity associativity, boolean unary) {
        this(symbol, priority, associativity, unary, false, false);
    }

    public final Associativity associativity; //defines in which way Shunting Yard algorithm gonna process this operator.
    public final String symbol; //ex: "+".
    public final byte priority; //0 = max priority;
    public final boolean unary; //defines if this operator operates on one term or two.
    public final boolean word; //defines if this operator should be recognized if his symbol is bounded with one or more white spaces (regex gonna be : \s+symbol\s+).
    public final boolean postfixed; //defines if this operator is already postfixed in the expression or not.

    public Order order = Order.LEFT_TO_RIGHT;

    public ScriptOperator setOrder(Order order) {
        ScriptOperator clone = clone();
        clone.order = order;
        return clone;
    }

    protected ScriptOperator clone() {
        return new ScriptOperator(symbol, priority, associativity, unary, word, postfixed);
    }

    @Override
    public String toString() {
        return symbol + (unary ? " (unary) " : "") + (word ? " (word) " : "");
    }

    public enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        NONE
    }

    public enum Order {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }
}
