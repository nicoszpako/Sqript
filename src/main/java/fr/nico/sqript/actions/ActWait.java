package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptTimer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeDate;

@Action(name = "Wait Actions",
        features = {
            @Feature(name = "Wait/delay",description = "Delays the execution of the script of a specific amount of time.",examples = "wait 10 seconds",pattern = "(wait|delay) {date}")
        }
)
public class ActWait extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        TypeDate delay = (TypeDate) getParameters().get(0).get(context);
        ScriptClock clock = new ScriptClock(context);
        clock.current = this.next;
        ScriptTimer.addDelay(clock,delay.getObject());
    }


    //We definitely stop the current execution to return the context
    @Override
    public IScript getNext(ScriptContext context) {
        return null;
    }

    @Override
    public IScript getParent() {
        return null;
    }
}
