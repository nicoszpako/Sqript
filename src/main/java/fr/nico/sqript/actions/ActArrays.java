package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.interfaces.IIndexedCollection;

import java.util.Collections;

@Action(name = "Arrays Actions",
        features = {
        @Feature(name = "Shuffle array", description = "Randomly shuffles the elements of an array.",examples = "shuffle [1,2,3,4,5,6]", pattern = "shuffle {array}"),
        @Feature(name = "Add an array to another array", description = "Adds the elements of an array to another array.",examples = "add [1,2,3] to [4,5,6]", pattern = "add elements of {array} to {array}"),
        @Feature(name = "Remove an array from an array", description = "Removes the elements of an array from another array.",examples = "remove [\"a\",\"b\"] from [\"a\",\"b\",\"c\"]\n", pattern = "remove elements of {array} from {array}"),
        @Feature(name = "Sort elements of an array", description = "Sort an array in ascending or descending order if their elements are comparable. Dictionaries can also be sorted, in this case by default they are sorted by keys but you can also sort them by values.",examples = "sort elements of [1,5,7,4,2,3]\n", pattern = "sort ((2;elements)|(3;keys)) of {array} [by] [((0;ascending)|(1;descending))] [order]"),
        },
        priority=1
)
public class ActArrays extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        ScriptExpression e1 = this.getParameters().get(0);
        switch (getMatchedIndex()){
            case 0:
                TypeArray array = (TypeArray) e1.get(context,new ScriptType[0]);
                Collections.shuffle(array.getObject());
                break;
            case 1:
                ScriptType s1 = e1.get(context);
                ScriptExpression e2 = this.getParameters().get(1);
                ScriptType s2 = e2.get(context);
                if(s1 instanceof TypeArray){
                    array = (TypeArray) s1;
                    TypeArray array1 = (TypeArray) e2.get(context,new ScriptType[0]);
                    array1.getObject().addAll(array.getObject());
                }else if(s1 instanceof TypeDictionary){
                    TypeDictionary dic = (TypeDictionary) s1;
                    TypeDictionary dic2 = (TypeDictionary) e2.get(context,new ScriptType[0]);
                    dic.getObject().putAll(dic2.getObject());
                }

                break;
            case 2:
                s1 = e1.get(context);
                e2 = this.getParameters().get(1);
                s2 = e2.get(context);
                if(s1 instanceof TypeArray){
                    array = (TypeArray) s1;
                    TypeArray array1 = (TypeArray) e2.get(context,new ScriptType[0]);
                    array1.getObject().removeAll(array.getObject());
                }else if(s1 instanceof TypeDictionary){
                    TypeDictionary dic = (TypeDictionary) s1;
                    TypeDictionary dic2 = (TypeDictionary) e2.get(context,new ScriptType[0]);
                    dic2.getObject().forEach((a,b)->dic.getObject().remove(a));
                }
                break;
            case 3:
                IIndexedCollection collection = (IIndexedCollection) getParameter(1).get(context);
                collection.sort(getMarks());
                break;

        }

    }
}
