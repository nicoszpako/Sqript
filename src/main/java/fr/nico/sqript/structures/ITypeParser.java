package fr.nico.sqript.structures;

import fr.nico.sqript.types.ScriptType;

@FunctionalInterface
public interface ITypeParser<T extends ScriptElement<?>,U> {

    U parse(T type);

}
