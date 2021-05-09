package fr.nico.sqript.expressions;


import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeDate;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.Arrays;

@Expression(name = "Date Expressions",
        description = "Manipulate dates",
        examples = "15 minutes and 4 seconds",
        patterns = {
                "[{number} year[s]] [[and] {number} month[s]] [[and] {number} week[s]] [[and] {number} day[s]] [[and] {number} hour[s]] [[and] {number} minute[s]] [[and] {number} second[s]] [[and] {number} tick[s]]",
                "now"
        })
public class ExprDate extends ScriptExpression{


        @Override
        public ScriptType get(ScriptContext context, ScriptType[] parameters) {
            //System.out.println("Date parameters :" + Arrays.toString(parameters));
            switch (getMatchedIndex()){
                case 0:
                    long total = 0;
                    int i=0;
                    for(ScriptType t: parameters){
                        if(t!=null && t instanceof TypeNumber){
                            long mult = 1;
                            //System.out.println(i);
                            switch(i){
                                case 0:
                                    mult= 20L *60*60*24*7*365; //year
                                    break;
                                case 1:
                                    mult=20*60*60*24*7*30; //month
                                    break;
                                case 2:
                                    mult=20*60*60*24*7; //week
                                    break;
                                case 3:
                                    mult=20*60*60*24; //day
                                    break;
                                case 4:
                                    mult=20*60*60; //hour
                                    break;
                                case 5:
                                    mult=20*60; //minute
                                    break;
                                case 6:
                                    mult=20; //second
                                    break;
                                case 7:
                                    mult=1; //tick
                                    break;
                            }
                            //System.out.println(mult);
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
