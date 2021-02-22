package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;

@Action(name = "Gui Actions",
        description ="Gui related actions",
        examples = {"make player open a complex gui"
        },
        patterns = {
            "make player open a[n] ((0;inventory)|(1;complex)) gui"
        }
)
public class ActGui extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                System.out.println("inventory ? "+getMarkValue(0));
                System.out.println("complex ? "+getMarkValue(1));

                return;
        }

    }
}
