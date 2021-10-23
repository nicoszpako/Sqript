package fr.nico.sqript.structures;

import fr.nico.sqript.types.ScriptType;

@FunctionalInterface
public interface ITypeParser {

    ScriptType<?> parse(ScriptType<?> type);

}
