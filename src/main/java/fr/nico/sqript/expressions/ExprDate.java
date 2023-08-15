package fr.nico.sqript.expressions;


import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeDate;
import fr.nico.sqript.types.primitive.TypeNumber;

@Expression(name = "Date Expressions",
        features = {
                @Feature(name = "Year", description = "Returns the given amount of years.", examples = "1 year and 5 months", pattern = "{number} year[s] [[and] {date}]", type = "date"),
                @Feature(name = "Month", description = "Returns the given amount of months.", examples = "1 month and 5 weeks", pattern = "{number} month[s] [[and] {date}]", type = "date"),
                @Feature(name = "Day", description = "Returns the given amount of days.", examples = "1 day and 5 hours", pattern = "{number} day[s] [[and] {date}]", type = "date"),
                @Feature(name = "Hour", description = "Returns the given amount of hours.", examples = "1 hour and 5 minutes", pattern = "{number} hour[s] [[and] {date}]", type = "date"),
                @Feature(name = "Minute", description = "Returns the given amount of minutes.", examples = "1 minute and 5 seconds", pattern = "{number} minute[s] [[and] {date}]", type = "date"),
                @Feature(name = "Second", description = "Returns the given amount of seconds.", examples = "1 second and 5 ticks", pattern = "{number} second[s] [[and] {date}]", type = "date"),
                @Feature(name = "Tick", description = "Returns the given amount of ticks.", examples = "1 tick", pattern = "{number} tick[s] [[and] {date}]", type = "date"),
                @Feature(name = "Now", description = "Returns the current date.", examples = "now", pattern = "now", type = "date")
        })
public class ExprDate extends ScriptExpression{


        @Override
        public ScriptType get(ScriptContext context, ScriptType[] parameters) {
            //System.out.println("Date parameters :" + Arrays.toString(parameters));

            switch (getMatchedIndex()){
                case 0:
                    int amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate(amount*20L *60*60*24*7*365);
                case 1:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20*60*60*24*7*30);
                case 2:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20*60*60*24*7);
                case 3:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20*60*60*24);
                case 4:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20*60*60);
                case 5:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20*60);
                case 6:
                    amount = ((Double)parameters[0].getObject()).intValue();
                    return new TypeDate((long) amount *20);
                case 7:
                    return new TypeDate(System.currentTimeMillis());
            }
            return null;
        }

        @Override
        public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
        }
}
