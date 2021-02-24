package fr.nico.sqript.types;

public interface IIndexedCollection {

    public ScriptType<?> get(int index);

    public int size();

}
