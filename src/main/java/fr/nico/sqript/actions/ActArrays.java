package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.interfaces.IIndexedCollection;

import java.util.Collections;
import java.util.Comparator;

@Action(name = "Arrays Actions",
        description ="Arrays relative actions",
        examples = "shuffle array",
        patterns = {
            "shuffle {array}",
            "add elements of {array} to {array}",
            "remove elements of {array} from {array}",
            "sort ((2;elements)|(3;keys)) of {array} [by] [(0;ascending)|(1;descending)] [order]"

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
