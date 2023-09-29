package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.primitive.TypeString;

@Action(name = "Print Actions", features =
        @Feature(name = "Replace substrings of a string", description = "Replace each given substring replaced by another string in a string.", examples = "replace each \"_\" with \" \" in \"Hello_world\"", pattern = "replace each {string} (by|with) {string} in {string}", type = "string")
)
public class ActStrings extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                ScriptExpression firstParameter = getParameters().get(0);
                ScriptExpression secondParameter = getParameters().get(1);
                ScriptExpression thirdParameter = getParameters().get(2);
                String replaced = firstParameter.get(context).toString();
                String replacedBy = secondParameter.get(context).toString();
                String target = thirdParameter.get(context).toString();
                thirdParameter.set(context,new TypeString(target.replace(replaced,replacedBy)));
                break;
        }
    }
}
