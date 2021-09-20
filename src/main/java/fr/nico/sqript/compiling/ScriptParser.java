package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public class ScriptParser {

    public ScriptExpression parse(ScriptToken line){
        return null;
    }

    public boolean check(ExpressionPartialSolution partialSolution){
        return true;
    }

    public class ExpressionPartialSolution {

        private ScriptExpression expression;

        private ScriptExpression[] parameters;

        public ExpressionPartialSolution() {
        }

        public ExpressionPartialSolution(ScriptExpression expression) {
            this.expression = expression;
        }

        public ExpressionPartialSolution(ScriptExpression expression, ScriptExpression[] parameters) {
            this.expression = expression;
            this.parameters = parameters;
        }

        public ScriptExpression[] getParameters() {
            return parameters;
        }

        public void setParameters(ScriptExpression[] parameters) {
            this.parameters = parameters;
        }

        public ScriptExpression getExpression() {
            return expression;
        }

        public void setExpression(ScriptExpression expression) {
            this.expression = expression;
        }
    }

}
