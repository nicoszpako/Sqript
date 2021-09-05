package fr.nico.sqript.structures;


public class OperatorDefinition {

    private final IOperation operation;
    private final Class<? extends ScriptElement<?>> returnType;

    public OperatorDefinition(IOperation operation, Class<? extends ScriptElement<?>> returnType) {
        this.operation = operation;
        this.returnType = returnType;
    }

    public IOperation getOperation() {
        return operation;
    }

    public Class<? extends ScriptElement> getReturnType() {
        return returnType;
    }

}
