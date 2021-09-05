package fr.nico.sqript.structures;


public class ScriptParameterDefinition {

    final Class<? extends ScriptElement<?>> typeClass;

    //Whether or not it expects an undefined number of arguments (whether or not it can be an array)
    final boolean n_args;

    public Class<? extends ScriptElement<?>> getTypeClass() {
        return typeClass;
    }

    public boolean isN_args() {
        return n_args;
    }

    public ScriptParameterDefinition(Class<? extends ScriptElement<?>> typeClass, boolean n_args) {
        this.typeClass = typeClass;
        this.n_args = n_args;
    }

    @Override
    public String toString() {
        return typeClass.toString();
    }
}
