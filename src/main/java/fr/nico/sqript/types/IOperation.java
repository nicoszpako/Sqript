package fr.nico.sqript.types;

@FunctionalInterface
public interface IOperation {
    ScriptType<?> operate(ScriptType<?> o1, ScriptType<?> o2);
}
