package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeNBTTagCompound;

@Action(name = "NBTTagCompound Actions",
        features = {
                @Feature(name = "Remove tag in NBTTagCompound", description = "remove the tag from an nbttagcompound.",examples = "delete tag \"Tag\" of {nbttagcompound}", pattern = "delete tag {string} of {nbttagcompound}"),
        }
)
public class ActNBTTagCompound extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        ScriptExpression firstParameter = getParameters().get(0);
        switch (getMatchedIndex()){
            case 0:
                TypeNBTTagCompound nbtTagCompound = (TypeNBTTagCompound) getParameters().get(1).get(context);
                nbtTagCompound.getObject().removeTag((String) firstParameter.get(context).getObject());
                break;
        }
    }
}
