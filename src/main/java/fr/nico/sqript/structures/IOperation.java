package fr.nico.sqript.structures;

import fr.nico.sqript.types.ScriptType;

@FunctionalInterface
public interface IOperation {
    ScriptType<?> operate(ScriptType<?> o1, ScriptType<?> o2);
}
