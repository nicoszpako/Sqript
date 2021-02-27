package fr.nico.sqript.types.interfaces;

import fr.nico.sqript.types.ScriptType;

public interface IIndexedCollection {

    public ScriptType<?> get(int index);

    public int size();

}
