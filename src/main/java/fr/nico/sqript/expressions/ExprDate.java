package fr.nico.sqript.expressions;


import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeDate;
import fr.nico.sqript.types.primitive.TypeNumber;

@Expression(name = "Date Expressions",
        description = "Manipulate dates",
        examples = "15 minutes and 4 seconds",
        patterns = {
                "[{number} year[s]] [[and] {number} month[s]] [[and] {number} week[s]] [[and] {number} day[s]] [[and] {number} hour[s]] [[and] {number} minute[s]] [[and] {number} second[s]] [[and] {number} tick[s]] [[and] {number} (millisecond[s]|ms)]",
                "now"
        })
public class ExprDate extends ScriptExpression{


        @Override
        public ScriptType get(ScriptContext context, ScriptType[] parameters) {
            switch (getMatchedIndex()){
                case 0:
                    long total = 0;
                    int i=0;
                    for(ScriptType t: parameters){
                        if(t!=null && t instanceof TypeNumber){
                            long mult = 1;
                            System.out.println(i);
                            switch(i){
                                case 0:
                                    mult=86400L*365L*1000L;
                                    break;
                                case 1:
                                    mult=86400L*30L*1000L;
                                    break;
                                case 2:
                                    mult=86400L*7L*1000L;
                                    break;
                                case 3:
                                    mult=86400*1000;
                                    break;
                                case 4:
                                    mult=3600*1000;
                                    break;
                                case 5:
                                    mult=60*1000;
                                    break;
                                case 6:
                                    mult=1000;
                                    break;
                                case 7:
                                    mult=20;
                                    break;
                                case 8:
                                    mult=1;
                                    break;
                            }
                            System.out.println(mult);
                            TypeNumber number = (TypeNumber)t;
                            total+=number.getObject()*mult;
                        }
                        i++;
                    }
                    return new TypeDate(total);
                case 1:
                    return new TypeDate(System.currentTimeMillis());
            }
            return null;
        }

        @Override
        public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
        }
}
