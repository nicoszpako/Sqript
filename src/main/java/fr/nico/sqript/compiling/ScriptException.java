package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;

public class ScriptException extends Exception {

    ScriptLine line;
    String message;

    public ScriptLine getLine() {
        return line;
    }

    public ScriptException(ScriptLine line){
        this.line=line;
        this.message="";
    }

    public ScriptException(ScriptLine line, String message){
        this.line=line;
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static class TypeNotSavableException extends ScriptException {

        Class type;

        public TypeNotSavableException(Class type) {
            super(null);
            this.type=type;
        }

        @Override
        public String getMessage() {
            return "Type is not savable : "+type.getSimpleName();
        }
    }

    public static class ScriptInterfaceNotImplementedException extends ScriptException {

        Class interfaceClass,given;
        public ScriptInterfaceNotImplementedException(ScriptLine line,Class interfaceClass, Class given) {
            super(line);
            this.interfaceClass=interfaceClass;
            this.given = given;
        }

        @Override
        public String getMessage() {
            return line.text+" of type "+given.getSimpleName()+" does not implement the features of "+interfaceClass.getSimpleName()+". Thus this expression cannot be applied on it.";
        }
    }

    public static class ScriptPatternError extends Exception {

        String reason;

        public ScriptPatternError(String reason) {
            this.reason=reason;
        }

        @Override
        public String getMessage() {
            return reason;
        }
    }

    public static class ScriptNotEnoughArgumentException extends ScriptException {

        public ScriptNotEnoughArgumentException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Not enough arguments given to function: \n"+line;
        }
    }

    public static class ScriptUnknownEventException extends ScriptException {

        public ScriptUnknownEventException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown trigger: \n"+line;
        }
    }


    public static class ScriptUnknownFunctionException extends ScriptException {

        public ScriptUnknownFunctionException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Not function defined as following in the given script instance: \n"+line;
        }
    }

    public static class ScriptUnknownInstanceException extends ScriptException {

        public ScriptUnknownInstanceException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown script instance (no script file found with the given parameter): \n"+line;
        }
    }

    public static class ScriptUnknownActionException extends ScriptException {

        public ScriptUnknownActionException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown expression: \n"+line;
        }
    }

    public static class ScriptUnknownExpressionException extends ScriptException {


        public ScriptUnknownExpressionException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown expression or undefined variable: \n"+line;
        }
    }

    public static class ScriptTypeException extends ScriptException {

        Class wanted,given;

        public ScriptTypeException(ScriptLine line,Class wanted,Class given) {
            super(line);
            this.wanted=wanted;
            this.given=given;
        }

        @Override
        public String getMessage() {
            return "Type error, given type is "+given.getSimpleName()+", it should be "+wanted.getSimpleName()+": \n"+line;
        }
    }

    public static class ScriptBadVariableNameException extends ScriptException {


        public ScriptBadVariableNameException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "No whitespaces are allowed in variables names ! : \n"+line;
        }
    }

    public static class ScriptNotOperableException extends ScriptException {


        public ScriptNotOperableException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Type is not operable: \n"+line;
        }
    }

    public static class ScriptUnexpectedTokenException extends ScriptException {

        public ScriptUnexpectedTokenException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unexpected token: \n"+line;
        }
    }

    public static class ScriptUnknownBlockException extends ScriptException {

        public ScriptUnknownBlockException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown block head: \n"+line;
        }
    }

    public static class ScriptUnknownTokenException extends ScriptException {

        public ScriptUnknownTokenException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown token: \n"+line;
        }
    }

    public static class ScriptMissingTokenException extends ScriptException {

        public ScriptMissingTokenException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Missing token: \n"+line;
        }
    }

    public static class ScriptNonSettableException extends ScriptException {

        ScriptExpression e;

        public ScriptNonSettableException(ScriptLine line, ScriptExpression e) {
            super(line);
            this.e=e;
        }

        @Override
        public String getMessage() {
            return e.getClass().getSimpleName()+" is not settable for pattern n°"+e.getMatchedIndex()+":"+line;
        }
    }

    public static class ScriptIndentationErrorException extends ScriptException {

        public ScriptIndentationErrorException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Indentation error (make sure that you use tabs and not whitespaces !): "+line;
        }
    }

    public static class ScriptSyntaxException extends ScriptException {

        String detail;

        public ScriptSyntaxException(ScriptLine line,String detail) {
            super(line);
            this.detail=detail;
        }

        @Override
        public String getMessage() {
            return "Syntax error ("+detail+"): \n"+line;
        }
    }

    public static class ScriptUndefinedReferenceException extends ScriptException {

        public ScriptUndefinedReferenceException(ScriptLine line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return line.text+" is not recognized as a valid expression or as a reference to the context : \n"+line;
        }
    }

}
