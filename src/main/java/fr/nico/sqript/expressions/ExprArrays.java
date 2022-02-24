package fr.nico.sqript.expressions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.interfaces.IIndexedCollection;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Expression(name = "Arrays",
        features = {
                @Feature(name = "Array", description = "Instantiates a new array.", examples = "[3.141,2.718,\"Hello !\"]", pattern = "~[{+element*}~]", type = "array"),
                @Feature(name = "Size of an array", description = "Returns the size of an array.", examples = "size of [1,4,7] #Returns 3", pattern = "size of {array|dictionary}", type = "number"),
                @Feature(name = "Random element of an array", description = "Returns a random element of an array.", examples = "random element of [1,4,7]", pattern = "[a] random element of {array|dictionary}"),
                @Feature(name = "First element of an array", description = "Returns the first element of an array.", examples = "first element of [1,4,7]", pattern = "[the] first element of {array|dictionary}"),
                @Feature(name = "Last element of an array", description = "Returns the last element of an array.", examples = "last element of [1,4,7]", pattern = "[the] last element of {array|dictionary}"),
                @Feature(name = "Element of an array", description = "Returns the element at the given index of an array. Be careful, indices start at 0.", examples = "[7,1,0,2,6][0] #Returns 7", pattern = "{array|dictionary}~[{+element}~]"),
                @Feature(name = "Range of two number", description = "Returns an array of numbers between two boundaries.", examples = "numbers from 1 to 5 #Returns [1,2,3,4,5]", pattern = "numbers from {number} to {number}", type = "array"),
                @Feature(name = "Range of numbers", description = "Returns an array of numbers between 0 and a boundary.", examples = "numbers in range of 5 #Returns [0,1,2,3,4,5]", pattern = "[numbers in] range of {number}", type = "array"),
                @Feature(name = "Range of random numbers", description = "Returns an array of a random subset of numbers between 0 and a boundary.", examples = "random numbers in range of 10\n", pattern = "random numbers in range of {number}", type = "array"),
                @Feature(name = "Element is in an array", description = "Returns whether an element is in an array.", examples = "7 is in [1,5,9,12] #Returns false", pattern = "{element} is in {array|dictionary}", type = "boolean"),
                @Feature(name = "Element is not in an array", description = "Returns whether an element is not in an array.", examples = "7 is not in [1,5,9,12] #Returns true", pattern = "{element} is not in {array|dictionary}", type = "boolean"),
                @Feature(name = "Array contains an array", description = "Returns whether an array contains another array.", examples = "[1.5,4,5,8] contains [4,8] #Returns true", pattern = "{array|dictionary} contains {array|dictionary}", type = "boolean"),
                @Feature(name = "Sorted array", description = "Returns a sorted copy of an array. Elements will be sorted only if they are comparable.", examples = "sorted elements of [1,8,5] #Returns [1,5,8]", pattern = "sorted [((0;ascending)|(1;descending))] ((2;elements)|(3;keys)) of {array|dictionary}", type = "array"),
                @Feature(name = "Copy of an array", description = "Returns a copy of an array.", examples = "copy of [1,8,5]", pattern = "copy of {array}", type = "array"),
        },
        priority = -1
)
public class ExprArrays extends ScriptExpression {

    @Override
    public ScriptType<?> get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException.ScriptNullReferenceException {
        switch (getMatchedIndex()) {
            case 0://new array
                TypeArray array = new TypeArray();
                array.getObject().addAll(Arrays.stream(parameters).filter(Objects::nonNull).collect(Collectors.toList()));
                return array;
            case 1: //size of array
                IIndexedCollection a = (IIndexedCollection) parameters[0];
                return new TypeNumber(a.size());
            case 2: //random element of
                a = (IIndexedCollection) parameters[0];
                return a.get(new Random().nextInt(a.size()));
            case 3: //first element of
                a = (IIndexedCollection) parameters[0];
                return a.get(0);
            case 4: //last element of
                a = (IIndexedCollection) parameters[0];
                return a.get(a.size() - 1);
            case 5: //array[int] expression

                if (parameters[0] instanceof TypeArray) {
                    a = (IIndexedCollection) parameters[0];
                    TypeNumber n = (TypeNumber) parameters[1];
                    return a.get(n.getObject().intValue());
                } else if (parameters[0] instanceof TypeDictionary) {
                    TypeDictionary d = (TypeDictionary) parameters[0]; //A simple wrapper for the HashMap
                    if (parameters[1] instanceof TypeNull) {
                        throw new ScriptException.ScriptNullReferenceException(line);
                    }
                    //System.out.println("Getting in "+d +" for key : "+parameters[1]);
                    return d.getObject().get(parameters[1]);
                }
                break;
            case 6: //numbers from a to b
                array = new TypeArray();
                TypeNumber b1 = (TypeNumber) parameters[0];
                TypeNumber b2 = (TypeNumber) parameters[1];
                for (double i = b1.getObject(); i <= b2.getObject(); i++) {
                    array.getObject().add(new TypeNumber(i));
                }
                return array;
            case 7: //numbers in range of a
                array = new TypeArray();
                b1 = (TypeNumber) parameters[0];
                for (double i = 0; i < b1.getObject(); i++) {
                    array.getObject().add(new TypeNumber(i));
                }
                return array;
            case 8: //random numbers in range of a
                array = new TypeArray();
                b1 = (TypeNumber) parameters[0];
                for (double i = 0; i <= b1.getObject(); i++) {
                    array.getObject().add(new TypeNumber(i));
                }
                Collections.shuffle(array.getObject());
                return array;
            case 9: //element is in
                ScriptType<?> b = parameters[0];
                a = (IIndexedCollection) parameters[1];
                return new TypeBoolean(a.contains(b));
            case 10: //element is not in
                b = parameters[0];
                a = (IIndexedCollection) parameters[1];
                return new TypeBoolean(!a.contains(b));
            case 11://contains
                if(parameters[1] instanceof TypeNull || parameters[0] instanceof TypeNull)
                    return TypeBoolean.FALSE();
                IIndexedCollection a2 = (IIndexedCollection) parameters[1];
                IIndexedCollection a1 = (IIndexedCollection) parameters[0];
                for (int i = 0; i < a2.size(); i++) {
                    if(!a1.contains(a2.get(i)))
                        return TypeBoolean.FALSE();
                }
                return TypeBoolean.TRUE();
            case 12://sorted elements of
                a = (IIndexedCollection) parameters[0];
                return (ScriptType<?>) a.sort(marks);
            case 13://copy of array
                a = (IIndexedCollection) parameters[0];
                return (ScriptType<?>) a.copy();
        }
        return null;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException {
        switch (getMatchedIndex()) {
            case 5:
                TypeArray a = (TypeArray) parameters[0];
                TypeNumber b = (TypeNumber) parameters[1];
                if (b.getObject() < a.getObject().size()) {
                    a.getObject().set(b.getObject().intValue(), to);
                    return true;
                } else {
                    throw new ScriptException.ScriptIndexOutOfBoundsException(this.line);
                }
        }
        return false;
    }
}