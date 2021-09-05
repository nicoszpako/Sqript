package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;

import java.util.*;


public class ExprCompiledExpression extends ScriptExpression {

    public LinkedList<CompiledToken> out = new LinkedList<>();
    public ScriptToken in;

    public ScriptExpression[] operands;
    public ScriptOperator[] operators;

    public ExprCompiledExpression(ScriptToken infixedExpression, ScriptExpression[] operands, ScriptOperator[] operators) throws ScriptException.ScriptMissingTokenException {
        this.operands = operands;
        this.operators = operators;
        this.in = infixedExpression;
        shuntingYard();
    }


    private Stack<CompiledToken> pile = new Stack<>();

    public ExprCompiledExpression(ExpressionTree tree) {
        out = astToRPN(tree);
    }

    public void shuntingYard() throws ScriptException.ScriptMissingTokenException {
        //System.out.println("Compiling : "+in.text +" with "+ Arrays.stream(operands).map(a-> {            try {                return (a instanceof ExprPrimitive ? a.get(null).toString():"/")+" ";            } catch (ScriptException e) {                e.printStackTrace();            }            return "";        }).collect(Collectors.joining())+" with operators : "+ Arrays.toString(operators));
        //Shunting-yard algorithm implementation by Nico- to get a RPN ("Notation polonaise inversée") with an unfixed notation

        int c = 0;
        while (c < in.getText().length()) {//While there are tokens to be read
            char r = in.getText().charAt(c);
            //Argument separator
            if (r == ',') {
                while (!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) {
                    out.add(pile.pop());
                }
                if (pile.empty()) {
                    //System.out.println("Parenthesage error");
                }
            }
            //Operand
            if (r == '@' && (c == 0 || in.getText().charAt(c - 1) != '\\')) {
                //Eating full token;
                char n;

                //Skipping first parenthese
                c++;

                StringBuilder id = new StringBuilder();
                while (c + 1 < in.getText().length() && (n = in.getText().charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    id.append(n);
                }

                // Skipping separation character
                c++;

                StringBuilder arity = new StringBuilder();
                while (c + 1 < in.getText().length() && (n = in.getText().charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    arity.append(n);
                }
                int id_int = Integer.parseInt(id.toString());
                int arity_int = Integer.parseInt(arity.toString());

                //Skipping last parenthese
                c++;

                CompiledToken e = new CompiledToken(arity_int, operands[id_int]);
                if (arity_int > 0)
                    pile.add(e);
                else out.add(e);
            } else if (r == '#' && (c == 0 || in.getText().charAt(c - 1) != '\\')) {//Operator
                char n;

                //Skipping first parenthese
                c++;

                String id = "";
                while (c + 1 < in.getText().length() && (n = in.getText().charAt(c + 1)) <= '9' && n >= '0') {
                    c++;
                    id += n;
                }

                //Skipping last parenthese
                c++;

                int id_int = Integer.parseInt(id);
                ScriptOperator o1 = ScriptManager.operators.get(id_int);
                //System.out.println("#{"+id_int+"} is a "+o1);
                if (o1.postfixed) {
                    //Already post-fixed, we add it normally
                    out.add(new CompiledToken(o1));
                } else {
                    ScriptOperator o2;
                    if (!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) {
                        o2 = (operators[pile.peek().getOperatorId()]);
                        while ((!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) && (
                                (o1.associativity == ScriptOperator.Associativity.LEFT_TO_RIGHT
                                        && (o2 = operators[pile.peek().getOperatorId()]).priority >= o1.priority) ||
                                        (o1.associativity == ScriptOperator.Associativity.RIGHT_TO_LEFT
                                                && o1.priority < o2.priority))) {
                            out.add(pile.pop());
                        }
                    }
                    pile.push(new CompiledToken(o1));
                }
            } else if (r == '(') {
                pile.push(new CompiledToken(EnumTokenType.LEFT_PARENTHESIS));
            } else if (r == ')') {
                while (!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) {
                    out.add(pile.pop());
                }
                if (pile.empty()) {
                    throw new ScriptException.ScriptMissingTokenException(getLine());
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

    public static LinkedList<CompiledToken> astToRPN(ExpressionTree tree){
        System.out.println("Parsing astToRPN : "+tree.toString());
        LinkedList<CompiledToken> out = new LinkedList<>();
        if(tree.getType() == EnumTokenType.EXPRESSION){
            out.add(new CompiledToken(tree.getLeavesCount(), tree.getExpression()));
        } else if(tree.getType() == EnumTokenType.OPERATOR){
            out.add(new CompiledToken(tree.getOperator()));
        }
        if(tree.getLeaves() != null)
            for(ExpressionTree leaf : tree.getLeaves()){
                out.addAll(0, astToRPN(leaf));
            }
        return out;
    }

    public static ExpressionTree rpnToAST(LinkedList<CompiledToken> nodes){
        Stack<ExpressionTree> treeStack = new Stack<>();
        System.out.println("RPN to AST from rpn : "+nodes);
        while (!nodes.isEmpty()) {//While there are tokens to be read
            CompiledToken token = nodes.remove(0);
            if (token.getType() == EnumTokenType.EXPRESSION) {
                if(token.getArity() > 0){
                    ExpressionTree merged = new ExpressionTree(token.getExpression());
                    while(!treeStack.empty()){
                        merged.addLeave(treeStack.pop());
                    }
                    treeStack.push(merged);
                }else
                    treeStack.push(new ExpressionTree(token.expression));
            } else if (token.getType() == EnumTokenType.OPERATOR) {//Operator
                ScriptOperator operator = token.getOperator();
                ExpressionTree merged = new ExpressionTree(operator);
                while(!treeStack.empty()){
                    merged.addLeave(treeStack.pop());
                }
                treeStack.push(merged);
            }

        }
        return treeStack.pop();
    }

    public static LinkedList<CompiledToken> infixToRPN(List<ExpressionTree> nodes) throws ScriptException.ScriptMissingTokenException {
        System.out.println("Infix to RPN with : "+nodes);
        Stack<CompiledToken> pile = new Stack<>();
        LinkedList<CompiledToken> out = new LinkedList<>();
        while (!nodes.isEmpty()) {//While there are tokens to be read
            ExpressionTree token = nodes.remove(0);
            //Operand
            if (token.getType() == EnumTokenType.EXPRESSION) {
                int arity = token.getLeavesCount();
                CompiledToken e = new CompiledToken(arity, token.getExpression());
                if (arity > 0)
                    pile.add(e);
                else out.add(e);
                System.out.println(out);
            } else if (token.getType() == EnumTokenType.OPERATOR) {//Operator
                ScriptOperator operator = token.getOperator();
                System.out.println("Operator is : "+operator);
                if (operator.postfixed) {
                    //Already post-fixed, we add it normally
                    out.add(new CompiledToken(operator));
                } else {
                    ScriptOperator operator2;
                    if (!pile.empty() && pile.peek().type == EnumTokenType.OPERATOR) {
                        operator2 = pile.peek().getOperator();
                        while ((!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) && (
                                (operator.associativity == ScriptOperator.Associativity.LEFT_TO_RIGHT
                                        && (operator2 = pile.peek().getOperator()).priority >= operator.priority) ||
                                        (operator.associativity == ScriptOperator.Associativity.RIGHT_TO_LEFT
                                                && operator.priority < operator2.priority))) {
                            out.add(pile.pop());
                        }
                    }
                    System.out.println("Before pile is : "+pile);
                    pile.push(new CompiledToken(operator));
                    System.out.println("Pusing on pile : "+operator+" = "+pile+" ("+pile.size()+")");
                }
                System.out.println(out);
            } else if (token.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                pile.push(new CompiledToken(EnumTokenType.LEFT_PARENTHESIS));
            } else if (token.getType() == EnumTokenType.RIGHT_PARENTHESIS) {
                while (!pile.empty() && pile.peek().type != EnumTokenType.LEFT_PARENTHESIS) {
                    out.add(pile.pop());
                }
                if (pile.empty()) {
                    throw new ScriptException.ScriptMissingTokenException(nodes.get(0).getExpression().getLine());
                }
                pile.pop();
                if (!pile.isEmpty() && pile.peek().arity > 0) {
                    out.add(pile.pop());
                }
            }
        }
        System.out.println("Emptying pile : "+pile);
        while (!pile.empty()) out.add(pile.pop());
        System.out.println("Returning : "+out);
        return out;
    }


    public String getRPN() {
        String r = "";
        for (CompiledToken s : out) {
            r += s;
        }
        return r;
    }

    public String getFullRPN() {
        StringBuilder r = new StringBuilder();
        r.append("COMPILED ");
        for (CompiledToken s : out) {
            r.append(s.toString()).append(" ");
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
        return rpnToAST(out).getReturnType();
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        final Stack<ScriptType> terms = new Stack<>();
        System.out.println("Evaluating get at line "+line+" : " + out+" WITH ("+getFullRPN()+")");
        //Optimisation
        if (out.size() == 1) {
            return out.get(0).getExpression().get(context);
        } else for (CompiledToken s : out) {
            if (s.getType() == EnumTokenType.EXPRESSION) {
                final ScriptExpression se = s.getExpression();
                if (se == null) {
                    terms.push(null);
                } else {
                    final List<ScriptType<?>> arguments = new ArrayList<>();
                    //System.out.println("Token is : "+s +" with arity : "+s.arity);
                    for (int i = 0; i < s.getArity(); i++) {
                        //Handle when some parameters of an expression are empty
                        //System.out.println("Peeking is : "+terms.peek());
                        arguments.add(terms.isEmpty() ? null : terms.pop());
                    }
                    ScriptType<?> t = se.get(context, arguments.toArray(new ScriptType[0]));
                    if (t == null)
                        t = new TypeNull();
                    terms.push(t);
                }
            } else if (s.getType() == EnumTokenType.OPERATOR) {
                final ScriptOperator o = s.getOperator();
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
                    final ScriptType<?> result = ScriptManager.getUnaryOperation(o1.getClass(), o).getOperation().operate(o1, null);
                    terms.push(result);
                } else {
                    final ScriptType<?> o1 = terms.pop();
                    final ScriptType<?> o2 = terms.pop();
                    final Class<? extends ScriptType> c1 = o1 == null ? null : o1.getClass();
                    final Class<? extends ScriptType> c2 = o2 == null ? null : o2.getClass();
                    //System.out.println("Operating : '"+o1+"' with '"+o2+"' according to "+o);
                    IOperation operation = ScriptManager.getBinaryOperation(c2, c1, o).getOperation();
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
        } else for (CompiledToken s : out) {
            if (s.type == EnumTokenType.EXPRESSION) {
                final ScriptExpression se = operands[s.getOperatorId()];
                ////System.out.println(se);
                final List<ScriptType<?>> arguments = new ArrayList<>();
                if (!(s == out.getLast())) {
                    for (int i = 0; i < s.arity; i++) {
                        arguments.add(0, types.isEmpty() ? null : types.pop());
                    }
                    types.push(se == null ? null : se.get(context, arguments.toArray(new ScriptType[0])));
                }
                expressions.push(se);
            } else if (s.type == EnumTokenType.OPERATOR) {
                final ScriptOperator o = ScriptManager.operators.get(s.getOperatorId());
                if (o.unary) {
                    final ScriptType<?> o1 = types.pop();
                    final ScriptType<?> result = ScriptManager.getUnaryOperation(o1.getClass(), o).getOperation().operate(o1, null);
                    types.push(result);
                    expressions.push(new ExprResult(result));
                } else {
                    final ScriptType<?> o1 = types.pop();
                    final ScriptType<?> o2 = types.pop();
                    ScriptType<?> result = ScriptManager.getBinaryOperation(o2.getClass(), o1.getClass(), o).getOperation().operate(o2, o1);
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


    public static class CompiledToken {

        private final EnumTokenType type;
        private final int arity;
        private final ScriptExpression expression;
        private final int operatorId;

        public CompiledToken(EnumTokenType tokenType) {
            this.type = tokenType;
            this.arity = 0;
            this.expression = null;
            this.operatorId = -1;
        }

        @Override
        public String toString() {
            if(type == EnumTokenType.EXPRESSION){
                return expression == null ? "null" : expression.toString();
            }else
                return getOperator().toString();
        }

        public CompiledToken(int arity, ScriptExpression expression) {
            this.arity = arity;
            this.expression = expression;
            this.operatorId = -1;
            this.type = EnumTokenType.EXPRESSION;
        }

        public CompiledToken(ScriptOperator operator) {
            this.arity = 0;
            this.operatorId = ScriptManager.operators.indexOf(operator);
            this.type = EnumTokenType.OPERATOR;
            this.expression = null;
        }

        public EnumTokenType getType() {
            return type;
        }

        public int getArity() {
            return arity;
        }

        public ScriptExpression getExpression() {
            return expression;
        }

        public int getOperatorId() {
            return operatorId;
        }

        public ScriptOperator getOperator() {
            return ScriptManager.operators.get(operatorId);
        }
    }

    @Override
    public String toString() {
        return getFullRPN();
    }
}
