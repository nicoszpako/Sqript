package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.compiling.parsers.ScriptExpressionParser;
import fr.nico.sqript.meta.ExpressionDefinition;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;

import java.util.*;


public class ExprCompiledExpression extends ScriptExpression {

    public Node ast;
    public ScriptToken in;

    public ExprCompiledExpression(Node tree) {
        ast = tree;
        //System.out.println("AST : "+ast);
    }

    public static List<Node> astToRPN(Node tree) {
        //System.out.println("Parsing astToRPN : "+tree.toString());
        ArrayList<Node> out = new ArrayList<>();
        if (tree.getChildren() != null && !(tree instanceof NodeSwitch)) {
            for (Node child : tree.getChildren()) {
                if (child != null)
                    out.addAll(0, astToRPN(child));
                else out.add(0, null);
            }
            tree.setChildren(new Node[tree.getChildren().length]);
        }
        if (tree instanceof NodeExpression || tree instanceof NodeOperation) {
            out.add(tree);
        }
        return out;
    }

    public static List<Node> astToInfix(Node tree) {
        //System.out.println("Parsing astToRPN : "+tree.toString());
        ArrayList<Node> out = new ArrayList<>();
        if (tree instanceof NodeExpression || tree instanceof NodeOperation || tree instanceof NodeSwitch) {
            out.add(tree);
        }
        if (tree.getChildren() != null && !(tree instanceof NodeSwitch)) {
            out.add(new NodeParenthesis(EnumTokenType.LEFT_PARENTHESIS));
            for (Node child : tree.getChildren()) {
                if (child != null) {
                    out.addAll(astToInfix(child));
                } else {
                    out.add(null);
                }
            }
            out.add(new NodeParenthesis(EnumTokenType.RIGHT_PARENTHESIS));
            tree.setChildren(new Node[tree.getChildren().length]);
        }

        return out;
    }

    public static Node rpnToAST(List<Node> nodes) {
        Stack<Node> treeStack = new Stack<>();
        //System.out.println("RPN to AST from rpn : " + nodes);
        while (!nodes.isEmpty()) {//While there are tokens to be read
            Node node = nodes.remove(0);
            //System.out.println("Top is : "+node);
            if (node instanceof NodeExpression || node instanceof NodeSwitch || (node instanceof NodeOperation && node.getChildren() != null && node.getChildren().length > 0)) {
                if(node instanceof NodeSwitch || node instanceof NodeOperation){
                    //System.out.println("Pushing "+node+" into : "+treeStack);
                    treeStack.push(node);
                }else {
                    NodeExpression nodeExpression = (NodeExpression) node;
                    //System.out.println(nodeExpression+" arity is "+nodeExpression.getArity());
                    if (nodeExpression.getArity() > 0 && nodeExpression.childrenAreNull()) {
                        Node merged = new NodeExpression(nodeExpression.getExpression());
                        for (int i = 0; i < nodeExpression.getArity(); i++) {
                            if (!treeStack.empty())
                                merged.addChild(treeStack.pop());
                            else merged.addChild(null);
                        }
                        treeStack.push(merged);
                    } else {
                        //System.out.println("Pushing : "+nodeExpression);
                        treeStack.push(nodeExpression);
                    }
                    //System.out.println("Treestack is : "+treeStack);
                }

            } else if (node instanceof NodeOperation) {//Operator
                NodeOperation nodeOperation = (NodeOperation) node;
                ScriptOperator operator = nodeOperation.getOperator();
                Node merged = new NodeOperation(operator);
                //System.out.println("after operator treeStack:" + treeStack);
                Stack<Node> toMerge = new Stack<>();
                for (int i = 0; i < (operator.unary ? 1 : 2); i++) {
                    if (!treeStack.empty())
                        toMerge.add(treeStack.pop());
                    else
                        toMerge.add(null);
                }
                int j = toMerge.size();
                for (int i = 0; i < j; i++) {
                    merged.addChild(toMerge.pop());
                }
                treeStack.push(merged);
            }
        }
        //System.out.println("Actual stack : "+treeStack);
        return treeStack.pop();
    }

    public static LinkedList<Node> infixToRPN(List<Node> nodes) throws ScriptException.ScriptMissingTokenException {
        //System.out.println("Infix to RPN with : " + nodes);
        Stack<Node> pile = new Stack<>();
        LinkedList<Node> out = new LinkedList<>();
        while (!nodes.isEmpty()) {//While there are tokens to be read
            //System.out.println("Next : " + nodes + " pile is : " + pile);
            Node node = nodes.remove(0);
            //Operand
            if (node instanceof NodeExpression) {
                NodeExpression nodeExpression = (NodeExpression) node;
                if (nodeExpression.getArity() > 0 && nodeExpression.childrenAreNull())
                    pile.add(nodeExpression);
                else out.add(nodeExpression);
                //System.out.println("Out : " + out);
            }else if (node instanceof NodeSwitch) {
                out.add(node);
                //System.out.println("Out : " + out);
            } else if (node instanceof NodeOperation) {
                NodeOperation nodeOperation = (NodeOperation) node;
                ScriptOperator operator = nodeOperation.getOperator();
                //System.out.println("Operator is : "+operator);
                if (operator.postfixed) {
                    //Already post-fixed, we add it normally
                    out.add(nodeOperation);
                } else {
                    NodeOperation operator2 = null;
                    if (!pile.empty() && pile.peek() instanceof NodeOperation) {
                        operator2 = ((NodeOperation) pile.peek());
                        while ((operator.associativity == ScriptOperator.Associativity.LEFT_TO_RIGHT
                                && (operator2.getOperator().priority >= operator.priority))
                                || ((operator.associativity == ScriptOperator.Associativity.RIGHT_TO_LEFT
                                && operator.priority <= operator2.getOperator().priority))) {
                            out.add(pile.pop());
                            if (!pile.empty() && pile.peek() instanceof NodeOperation)
                                operator2 = ((NodeOperation) pile.peek());
                            else break;
                        }
                    }
                    //System.out.println("Before pile is : "+pile);
                    pile.push(nodeOperation);
                    //System.out.println("Pushing on pile : "+operator+" => "+pile+" ("+pile.size()+")");
                }
                //System.out.println("Out now : " + out);
            } else if (node instanceof NodeParenthesis) {
                NodeParenthesis nodeParenthesis = (NodeParenthesis) node;
                if (nodeParenthesis.getType() == EnumTokenType.LEFT_PARENTHESIS) {
                    pile.push(nodeParenthesis);
                    //System.out.println("Pushing left parenthesis : " + pile);
                } else {
                    //System.out.println("Right parenthesis, pile is " + pile);
                    while (!pile.empty() && !(pile.peek() instanceof NodeParenthesis && (((NodeParenthesis) (pile.peek())).getType() == EnumTokenType.LEFT_PARENTHESIS))) {
                        out.add(pile.pop());
                    }
                    if (pile.empty()) {
                        throw new ScriptException.ScriptMissingTokenException(nodes.get(0).getLine());
                    }
                    pile.pop();
                    if (!pile.isEmpty() && (pile.peek() instanceof NodeExpression && (((pile.peek())).getChildren().length > 0))) {
                        out.add(pile.pop());
                    }
                }
            }
        }
        //System.out.println("Emptying pile : "+pile);
        while (!pile.empty()) out.add(pile.pop());
        //System.out.println("Returning infix to rpn : " + out);
        return out;
    }

    public Class getReturnType() {
        return ast.getReturnType();
    }

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException, ScriptException.ScriptInterfaceNotImplementedException {
        return get(ast, context, null);
    }

    private ScriptType get(Node node, ScriptContext context, Class[] validTypes) throws ScriptException {
        //System.out.println("Getting for node : "+node+" wanting : "+Arrays.toString(validTypes));
        if (node instanceof NodeExpression) {
            ScriptExpression expression = ((NodeExpression) node).getExpression();
            ExpressionDefinition expressionDefinition = ScriptManager.getDefinitionFromExpression(expression.getClass());
            int arity = node.getChildren() == null ? 0 : node.getChildren().length;
            ScriptType[] types = new ScriptType[arity];
            //System.out.println("Children : "+ Arrays.toString(node.getChildren()));
            for (int i = 0; i < arity; i++) {
                types[i] = get(node.getChildren()[i], context, expressionDefinition == null ? new Class[]{ScriptElement.class} : expressionDefinition.transformedPatterns[expression.getMatchedIndex()].getValidTypes(i));
            }
            return expression.get(context, types);
        } else if (node instanceof NodeOperation) {
            ScriptOperator o = ((NodeOperation) node).getOperator();
            final ScriptType<?> o1 = get(node.getChildren()[0], context, new Class[]{ScriptElement.class});
            ScriptType<?> o2 = null;
            if (node.getChildren().length == 2)
                o2 = get(node.getChildren()[1], context, new Class[]{ScriptElement.class});
            if (o == ScriptOperator.EQUAL) {
                return (new TypeBoolean(o1.equals(o2)));
            } else if (o == ScriptOperator.NOT_EQUAL) {
                return (new TypeBoolean(!o1.equals(o2)));
            } else if (o.unary) {
                //System.out.println(o1);
                //System.out.println(o);
                //System.out.println(o1.getClass());
                return ScriptManager.getUnaryOperation(o1.getClass(), o).getOperation().operate(o1, null);
            } else {
                final Class<? extends ScriptType> c1 = o1 == null ? null : o1.getClass();
                final Class<? extends ScriptType> c2 = o2 == null ? null : o2.getClass();
                //System.out.println("Looking for operation between "+c1+" "+c2+" "+o);
                IOperation operation = ScriptManager.getBinaryOperation(c1, c2, o).getOperation();
                if (operation == null) {
                    throw new ScriptException.ScriptOperationNotSupportedException(getLine(), o, c1, c2);
                }
                return operation.operate(o1, o2);
            }
        } else if (node instanceof NodeSwitch) {
            if (validTypes == null) {
                for (Node branch : node.getChildren()) {
                    try {
                        ScriptType result = get(branch, context, null);
                        return result;
                    } catch (Exception ignored) {
                    }
                }
            } else {
                for (Node branch : node.getChildren()) {
                    if (ScriptExpressionParser.isTypeValid(branch.getReturnType(), validTypes)) {
                        ScriptType result = get(branch, context, null);
                        if (ScriptExpressionParser.isTypeValid(result.getClass(), validTypes))
                            return result;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        return set(ast, context, to);
    }

    private boolean set(Node ast, ScriptContext context, ScriptType to) throws ScriptException {
        //System.out.println("Setting with node : "+ast);
        if (ast instanceof NodeExpression) {
            ScriptExpression expression = ((NodeExpression) ast).getExpression();
            ExpressionDefinition expressionDefinition = ScriptManager.getDefinitionFromExpression(expression.getClass());
            int arity = ast.getChildren() == null ? 0 : ast.getChildren().length;
            ScriptType[] types = new ScriptType[arity];
            assert expressionDefinition != null;
            for (int i = 0; i < arity; i++) {
                types[i] = get(ast.getChildren()[i], context, expressionDefinition.transformedPatterns[expression.getMatchedIndex()].getValidTypes(i));
            }
            //System.out.println("Setting "+expression.getClass()+" with "+Arrays.toString(types));
            return expression.set(context, to, types);
        } else if (ast instanceof NodeSwitch) {
            for (Node n : ast.getChildren()) {
                if (n instanceof NodeExpression) {
                    return set(n, context, to);
                }
            }
        }
        return false;
    }

    /*
    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        final Stack<ScriptType> terms = new Stack<>();
        //System.out.println("Evaluating get at line "+line+" : " + getFullRPN());
        //Optimisation
        if (rpn.size() == 1) {
            return ((NodeExpression) rpn.get(0)).getExpression().get(context);
        } else for (Node s : rpn) {
            if (s instanceof NodeExpression) {
                final ScriptExpression se = ((NodeExpression)(s)).getExpression();
                if (se == null) {
                    terms.push(null);
                } else {
                    final List<ScriptType<?>> arguments = new ArrayList<>();
                    //System.out.println("Token is : "+s +" with arity : "+((NodeExpression) s).getArity());
                    for (int i = 0; i < ((NodeExpression)(s)).getArity(); i++) {
                        //Handle when some parameters of an expression are empty
                        //System.out.println("Peeking is : "+terms.peek());
                        arguments.add(terms.isEmpty() ? null : terms.pop());
                    }

                    ScriptType<?> t = se.get(context, arguments.toArray(new ScriptType[0]));
                    if (t == null)
                        t = new TypeNull();
                    terms.push(t);
                }
            } else if (s instanceof NodeOperation) {
                final ScriptOperator o = ((NodeOperation)(s)).getOperator();
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
    */

    /*
    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType<?>[] parameters) throws ScriptException {
        final Stack<ScriptExpression> expressions = new Stack<>();
        final Stack<ScriptType> types = new Stack<>();
        //System.out.println("Evaluating set : " + getFullRPN());
        if (rpn.size() == 1) {
            return ((NodeExpression)rpn.get(0)).getExpression().set(context, to, new ScriptType[0]);
        } else for (Node s : rpn) {
            if (s instanceof NodeExpression) {
                final ScriptExpression se = ((NodeExpression)(s)).getExpression();
                ////System.out.println(se);
                final List<ScriptType<?>> arguments = new ArrayList<>();
                if (!(s == rpn.get(rpn.size() - 1))) {
                    for (int i = 0; i < ((NodeExpression)(s)).getArity(); i++) {
                        arguments.add(0, types.isEmpty() ? null : types.pop());
                    }
                    types.push(se == null ? null : se.get(context, arguments.toArray(new ScriptType[0])));
                }
                expressions.push(se);
            } else if (s instanceof NodeOperation) {
                final ScriptOperator o = ((NodeOperation)(s)).getOperator();
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
     */

    @Override
    public String toString() {
        return "C:" + ast.toString();
    }
}
