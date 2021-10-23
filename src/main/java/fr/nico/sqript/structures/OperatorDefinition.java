package fr.nico.sqript.structures;


public class OperatorDefinition {

    private final IOperation operation;
    private final Class<? extends ScriptElement<?>> returnType;
    private final int priority;

    public OperatorDefinition(IOperation operation, Class<? extends ScriptElement<?>> returnType, int priority) {
        this.operation = operation;
        this.returnType = returnType;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public IOperation getOperation() {
        return operation;
    }

    public Class<? extends ScriptElement> getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "OperatorDefinition{" +
                "operation=" + operation +
                ", returnType=" + returnType +
                ", priority=" + priority +
                '}';
    }
}
