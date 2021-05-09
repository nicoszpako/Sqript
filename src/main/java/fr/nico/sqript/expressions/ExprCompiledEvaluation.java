package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;

import java.util.*;
import java.util.stream.Collectors;


public class ExprCompiledEvaluation extends ScriptExpression {

    public LinkedList<Token> out = new LinkedList<>();
    public ScriptLine in;

    public ScriptExpression[] operands;
    public ScriptOperator[] operators;

    public ExprCompiledEvaluation(ScriptLine infixedExpression, ScriptExpression[] operands, ScriptOperator[] operators) {
        this.operands = operands;
        this.operators = operators;
        this.in = infixedExpression;
        compile();
    }


    private Stack<Token> pile = new Stack<>();

    public void compile() {
        //System.out.println("Compiling : "+in.text +" with "+ Arrays.stream(operands).map(a-> {            try {                return (a instanceof ExprPrimitive ? a.get(null).toString():"/")+" ";            } catch (ScriptException e) {                e.printStackTrace();            }            return "";        }).collect(Collectors.joining())+" with operators : "+ Arrays.toString(operators));
        //Shunting-yard algorithm implementation by Nico- to get a RPN ("Notation polonaise inversée") with an unfixed notation
        int c = 0;
        while (c < in.text.length()) {//While there are tokens to be read
            char r = in.text.charAt(c);
            if (r == ',') {//Argument separator
                while (!pile.empty() && pile.peek().token_type != TOKEN_TYPE.LEFT_PARENTHESIS) {
                    out.add(pile.pop());
                }
                if (pile.empty()) {
                    ////System.out.println("Parenthesage error");
                }
            }
            if (r == '@' && (c == 0 || in.text.charAt(c - 1) != '\\')) {//Operand
                //Eating full token;

                char n;
                c++;//First "{"

                String id = "";
                while (c + 1 < in.text.length() && (n = in.text.charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    id += n;
                }
                c++;//"/" separator for arity
                String arity = "";
                while (c + 1 < in.text.length() && (n = in.text.charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    arity += n;
                }
                int id_int = Integer.parseInt(id);
                int arity_int = Integer.parseInt(arity);

                c++;//Last "}"
                Token e = new Token(TOKEN_TYPE.EXPRESSION, arity_int, id_int);
                if (arity_int > 0)
                    pile.add(e);
                else out.add(e);
            } else if (r == '#' && (c == 0 || in.text.charAt(c - 1) != '\\')) {//Operator
                char n;
                c++;//First "{"
                String id = "";
                while (c + 1 < in.text.length() && (n = in.text.charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    id += n;
                }
                c++;//Last "}"
                int id_int = Integer.parseInt(id);
                ScriptOperator o1 = operators[id_int];
                //System.out.println("#{"+id_int+"} is a "+o1);
                if (o1.postfixed) {//Already postfixed, we add it normally
                    out.add(new Token(TOKEN_TYPE.OPERATOR, 0, id_int));
                } else {
                    ScriptOperator o2 = null;
                    if (!pile.empty() && pile.peek().token_type != TOKEN_TYPE.LEFT_PARENTHESIS) {
                        o2 = (operators[pile.peek().id]);
                        while ((!pile.empty() && pile.peek().token_type != TOKEN_TYPE.LEFT_PARENTHESIS) && (
                                (o1.associativity == ScriptOperator.Associativity.LEFT_TO_RIGHT
                                        && (o2 = operators[pile.peek().id]).priority >= o1.priority) ||
                                        (o1.associativity == ScriptOperator.Associativity.RIGHT_TO_LEFT
                                                && o1.priority < o2.priority))) {
                            out.add(pile.pop());
                        }
                    }
                    pile.push(new Token(TOKEN_TYPE.OPERATOR, 0, id_int));
                }
            } else if (r == '(') {
                pile.push(new Token(TOKEN_TYPE.LEFT_PARENTHESIS, 0, 0));
            } else if (r == ')') {
                while (!pile.empty() && pile.peek().token_type != TOKEN_TYPE.LEFT_PARENTHESIS) {
                    out.add(pile.pop());
                }
                if (pile.empty()) {
                    ////System.out.println("Parenthesis error");
                }
                pile.pop();

                if (!pile.isEmpty() && pile.peek().arity > 0) {
                    out.add(pile.pop());
                }

            }
            c++;
        }
        while (!pile.empty()) out.add(pile.pop());
        //System.out.println(getFullRPN());;
    }

    public String getRPN() {
        String r = "";
        for (Token s : out) {
            r += s;
        }
        return r;
    }

    public String getFullRPN() {
        StringBuilder r = new StringBuilder();
        for (Token s : out) {
            r.append(s.token_type == TOKEN_TYPE.EXPRESSION ? "[" + (operands[s.id] == null ? "null" : operands[s.id].getClass().getSimpleName()) + " " + s.id + ":" + (operands[s.id] == null ? 0 : operands[s.id].getMatchedIndex()) + "]" : operators[s.id].symbol).append(" ");
        }
        return r.toString();
    }

    public int getOpCode(String symbol, boolean binary) {
        for (int i = 0; i < ScriptManager.operators.size(); i++) {
            ScriptOperator o = operators[i];
            if (o.symbol.equals(symbol) && (o.unary == binary)) return i;
        }
        return -1;
    }

    public Class<? extends ScriptElement> getReturnType() {
        return ScriptElement.class;
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        final Stack<ScriptType> terms = new Stack<>();
        //System.out.println("Evaluating get at line "+line.getNumber()+" : " + out+" WITH ("+getFullRPN()+")");
        //Optimisation
        if (out.size() == 1) {
            return operands[0].get(context, null);
        } else for (Token s : out) {
            if (s.token_type == TOKEN_TYPE.EXPRESSION) {
                final ScriptExpression se = operands[s.id];
                if (se == null) {
                    terms.push(null);
                } else {
                    final List<ScriptType<?>> arguments = new ArrayList<>();
                    //System.out.println("Token is : "+s +" with arity : "+s.arity);
                    for (int i = 0; i < s.arity; i++) {
                        //Handle when some parameters of an expression are empty
                        //System.out.println("Peeking is : "+terms.peek());
                        arguments.add(0, terms.isEmpty() ? null : terms.pop());
                    }
                    ScriptType<?> t = se.get(context, arguments.toArray(new ScriptType[0]));
                    if (t == null)
                        t = new TypeNull();
                    terms.push(t);
                }
            } else if (s.token_type == TOKEN_TYPE.OPERATOR) {
                final ScriptOperator o = operators[s.id];
                //System.out.println("Operator is : "+o);
                if (o == ScriptOperator.EQUAL) {
                    final ScriptType<?> o1 = terms.pop();
                    final ScriptType<?> o2 = terms.pop();
                    terms.push(new TypeBoolean(o1.equals(o2)));
                } else if (o == ScriptOperator.NOT_EQUAL) {
                    final ScriptType<?> o1 = terms.pop();
                    final ScriptType<?> o2 = terms.pop();
                    //System.out.println("CHECKING IF NOT EQUALS : "+o1 + " "+o2);
                    terms.push(new TypeBoolean(!o1.equals(o2)));
                } else if (o.unary) {
                    final ScriptType<?> o1 = terms.pop();
                    final ScriptType<?> result = ScriptManager.getUnaryOperation(o1.getClass(), o).operate(o1, null);
                    terms.push(result);
                } else {
                    final ScriptType<?> o1 = terms.pop();
                    final ScriptType<?> o2 = terms.pop();
                    final Class<? extends ScriptType> c1 = o1 == null ? null : o1.getClass();
                    final Class<? extends ScriptType> c2 = o2 == null ? null : o2.getClass();
                    //System.out.println("Operating : '"+o1+"' with '"+o2+"' according to "+o);
                    IOperation operation = ScriptManager.getBinaryOperation(c2, c1, o);
                    if (operation == null) {
                        throw new ScriptException.ScriptOperationNotSupportedException(getLine(), o, c1, c2);
                    }
                    terms.push(operation.operate(o2, o1));
                }
            }
        }
        return terms.pop();
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        final Stack<ScriptExpression> expressions = new Stack<>();
        final Stack<ScriptType> types = new Stack<>();
        ////System.out.println("Evaluating set : " + getFullRPN());
        if (out.size() == 1) {
            return operands[0].set(context, to, new ScriptType[0]);
        } else for (Token s : out) {
            if (s.token_type == TOKEN_TYPE.EXPRESSION) {
                final ScriptExpression se = operands[s.id];
                ////System.out.println(se);
                final List<ScriptType<?>> arguments = new ArrayList<>();
                if (!(s == out.getLast())) {
                    for (int i = 0; i < s.arity; i++) {
                        arguments.add(0, types.isEmpty() ? null : types.pop());
                    }
                    types.push(se == null ? null : se.get(context, arguments.toArray(new ScriptType[0])));
                }
                expressions.push(se);
            } else if (s.token_type == TOKEN_TYPE.OPERATOR) {
                final ScriptOperator o = ScriptManager.operators.get(s.id);
                if (o.unary) {
                    final ScriptType<?> o1 = types.pop();
                    final ScriptType<?> result = ScriptManager.getUnaryOperation(o1.getClass(), o).operate(o1, null);
                    types.push(result);
                    expressions.push(new ExprResult(result));
                } else {
                    final ScriptType<?> o1 = types.pop();
                    final ScriptType<?> o2 = types.pop();
                    ScriptType<?> result = ScriptManager.getBinaryOperation(o2.getClass(), o1.getClass(), o).operate(o2, o1);
                    if (result == null) {//If r is null, we check if a generic operation is defined for the operator
                        ScriptManager.log.error("Operation : '" + o + "' with " + o1.getClass().getSimpleName() + " is not supported by " + o2.getClass().getSimpleName() + " and reciprocally");
                    }
                    types.push(result);
                    expressions.push(new ExprResult(result));
                }

            }
        }
        ////System.out.println("Pop is: "+expressions.peek().getClass().getSimpleName());
        return expressions.pop().set(context, to, types.toArray(new ScriptType[0]));
    }


    public static class Token {

        final TOKEN_TYPE token_type;
        final int arity;
        final int id;

        @Override
        public String toString() {
            return token_type +
                    "{" +
                    (token_type == TOKEN_TYPE.OPERATOR ? ScriptManager.operators.get(id).symbol : id) +
                    '}';
        }

        public Token(TOKEN_TYPE token_type, int arity, int id) {
            this.token_type = token_type;
            this.arity = arity;
            this.id = id;
        }
    }

    public enum TOKEN_TYPE {
        EXPRESSION,
        OPERATOR,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS
    }

}
