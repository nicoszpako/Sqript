package fr.nico.sqript.types.interfaces;

import java.util.Comparator;

public interface IComparable<T> {
    default int compare(T a, T b) {
        return 0;
    }
}
