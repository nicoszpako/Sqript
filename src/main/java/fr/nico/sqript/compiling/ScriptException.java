package fr.nico.sqript.compiling;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptException extends Exception {

    ScriptToken line;
    String message;

    public ScriptToken getLine() {
        return line;
    }

    public ScriptException(ScriptToken line) {
        this.line = line;
        this.message = "";
    }

    public ScriptException(ScriptToken line, String message) {
        this.line = line;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return line + " " + message;
    }

    public static class ScriptExceptionList extends ScriptException {

        public List<Throwable> exceptionList = new ArrayList<>();

        public ScriptExceptionList() {
            super(null);
        }

        @Override
        public String getMessage() {
            StringBuilder r = new StringBuilder();
            for (Throwable exception : exceptionList) {
                if (exception instanceof ScriptException) {
                    r.append(exception.getMessage()).append("\n");
                } else
                    r.append("Fatal exception : " + exception.getMessage());
                    exception.printStackTrace();
            }
            return r.toString();
        }
    }

    public static class ScriptWrappedException extends ScriptException {

        public Exception getWrapped() {
            return wrapped;
        }

        Exception wrapped;

        public ScriptWrappedException(ScriptToken line, Exception e) {
            super(line);
            wrapped = e;
        }

        @Override
        public String getMessage() {
            return line + " " + wrapped;
        }
    }

    public static class ScriptTypeNotSaveableException extends ScriptException {

        Class type;
        String key;
        public ScriptTypeNotSaveableException(String key, Class type) {
            super(null);
            this.type = type;
            this.key = key;
        }

        @Override
        public String getMessage() {
            return key+"'s type is not savable : " + type.getSimpleName();
        }
    }

    public static class ScriptInterfaceNotImplementedException extends ScriptException {

        Class interfaceClass, given;

        public ScriptInterfaceNotImplementedException(ScriptToken line, Class interfaceClass, Class given) {
            super(line);
            this.interfaceClass = interfaceClass;
            this.given = given;
        }

        @Override
        public String getMessage() {
            return line.getText() + " of type " + given.getSimpleName() + " does not implement the features of " + interfaceClass.getSimpleName() + ". Thus this expression cannot be applied on it.";
        }
    }

    public static class ScriptEmptyExpressionException extends ScriptException {

        public ScriptEmptyExpressionException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Empty expression at line : " + line;
        }
    }

    public static class ScriptEmptyLoopException extends ScriptException {

        public ScriptEmptyLoopException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Empty loop at line : " + line;
        }
    }

    public static class ScriptPatternError extends Exception {

        String reason;

        public ScriptPatternError(String reason) {
            this.reason = reason;
        }

        @Override
        public String getMessage() {
            return reason;
        }
    }

    public static class ScriptNotEnoughArgumentException extends ScriptException {

        public ScriptNotEnoughArgumentException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Not enough arguments given to function: \n" + line;
        }
    }

    public static class ScriptUnknownEventException extends ScriptException {

        public ScriptUnknownEventException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown trigger: \n" + line;
        }
    }


    public static class ScriptUnknownFunctionException extends ScriptException {

        public ScriptUnknownFunctionException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Not function defined as following in the given script instance: \n" + line;
        }
    }

    public static class ScriptUnknownInstanceException extends ScriptException {

        public ScriptUnknownInstanceException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown script instance (no script file found with the given parameter): \n" + line;
        }
    }

    public static class ScriptUnknownActionException extends ScriptException {

        public ScriptUnknownActionException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown expression: \n" + line;
        }
    }

    public static class ScriptUnknownExpressionException extends ScriptException {


        public ScriptUnknownExpressionException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown expression or undefined variable: \n" + line;
        }
    }

    public static class ScriptTypeException extends ScriptException {

        Class[] wanted;
        Class given;

        public ScriptTypeException(ScriptToken line, Class[] wanted, Class given) {
            super(line);
            this.wanted = wanted;
            this.given = given;
        }

        @Override
        public String getMessage() {
            return "Type error, given type is " + given.getSimpleName() + ", it should be " + Arrays.toString(wanted) + ": \n" + line;
        }
    }

    public static class ScriptBadVariableNameException extends ScriptException {


        public ScriptBadVariableNameException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "No whitespaces are allowed in variables names ! : \n" + line;
        }
    }

    public static class ScriptNotOperableException extends ScriptException {


        public ScriptNotOperableException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Type is not operable: \n" + line;
        }
    }

    public static class ScriptUnexpectedTokenException extends ScriptException {

        public ScriptUnexpectedTokenException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unexpected token: \n" + line;
        }
    }

    public static class ScriptUnknownBlockException extends ScriptException {

        public ScriptUnknownBlockException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown block head: \n" + line;
        }
    }

    public static class ScriptUnknownTokenException extends ScriptException {

        public ScriptUnknownTokenException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Unknown token: \n" + line;
        }
    }

    public static class ScriptMissingFieldException extends ScriptException {

        String requiredFieldName;
        String blockName;

        public ScriptMissingFieldException(ScriptToken line, String blockName, String requiredFieldName) {
            super(line);
            this.requiredFieldName = requiredFieldName;
            this.blockName = blockName;
        }

        @Override
        public String getMessage() {
            return "Field \"" + requiredFieldName + "\" for block \"" + blockName + "\" cannot be undefined: \n" + line;
        }
    }


    public static class ScriptMissingClosingTokenException extends ScriptException {


        public ScriptMissingClosingTokenException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "A closing token is missing (it could be : '}' ')' ']' or '%') : \n" + getLine();
        }
    }

    public static class ScriptMissingTokenException extends ScriptException {

        public ScriptMissingTokenException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Missing token: \n" + line;
        }
    }

    public static class ScriptNonSettableException extends ScriptException {

        ScriptExpression e;

        public ScriptNonSettableException(ScriptToken line, ScriptExpression e) {
            super(line);
            this.e = e;
        }

        @Override
        public String getMessage() {
            return e.getClass().getSimpleName() + " is not settable for pattern n°" + e.getMatchedIndex() + ":" + line;
        }
    }

    public static class ScriptIndexOutOfBoundsException extends ScriptException {

        public ScriptIndexOutOfBoundsException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Array index out of bounds : " + line;
        }
    }

    public static class ScriptIndentationErrorException extends ScriptException {

        public ScriptIndentationErrorException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return "Indentation error (make sure that you use tabs and not whitespaces !): " + line;
        }
    }

    public static class ScriptSyntaxException extends ScriptException {

        String detail;

        public ScriptSyntaxException(ScriptToken line, String detail) {
            super(line);
            this.detail = detail;
        }

        @Override
        public String getMessage() {
            return "Syntax error (" + detail + "): \n" + line;
        }
    }

    public static class ScriptNullReferenceException extends ScriptException {

        public ScriptNullReferenceException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return line.getText() + " contains a value which has not been declared or which is null in this special context : \n" + line;
        }
    }

    public static class ScriptUndefinedReferenceException extends ScriptException {

        public ScriptUndefinedReferenceException(ScriptToken line) {
            super(line);
        }

        @Override
        public String getMessage() {
            return line.getText() + " is not recognized as a valid expression or as a reference to the context : \n" + line;
        }
    }

    public static class ScriptOperationNotSupportedException extends ScriptException {

        ScriptOperator o;
        Class a, b;

        public ScriptOperationNotSupportedException(ScriptToken line, ScriptOperator o, Class<? extends ScriptType> b, Class<? extends ScriptType> a) {
            super(line);
            this.o = o;
            this.a = a;
            this.b = b;
        }

        @Override
        public String getMessage() {
            return line + " Operation : '" + o + "' with " + (b != null ? b.getSimpleName() : "null") + " is not supported by " + (a != null ? a.getSimpleName() : "null");
        }
    }

    public static class ScriptBadSideException extends ScriptException {

        Side side;

        public ScriptBadSideException(ScriptToken line, Side side) {
            super(line);
            this.side = side;
        }

        @Override
        public String getMessage() {
            return "Bad execution side : "+side+" at "+line;
        }
    }
}
