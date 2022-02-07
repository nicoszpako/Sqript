package fr.nico.sqript.types.interfaces;

import fr.nico.sqript.types.ScriptType;

public interface IIndexedCollection {

    public boolean contains(ScriptType<?> t);

    public ScriptType<?> get(int index);

    public IIndexedCollection sort(int mode);

    public IIndexedCollection copy();

    public int size();

}
