package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;

@Action(name = "Return Actions",
        description ="Return values and cancel events",
        examples = "return cos(x)",
        patterns = {
        "return {element}",
        "cancel event",
        "break loop",
        "break {number} loops"
        }
)
public class ActReturn extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch(getMatchedIndex()){
            case 0:
                context.setReturnValue(getParameters().get(0).get(context));
                return;
            case 1:
                context.setReturnValue(TypeBoolean.TRUE());
                return;
            case 2:
                IScript p = super.parent;
                while(!(p instanceof ScriptLoop.ScriptLoopWHILE) && !(p instanceof ScriptLoop.ScriptLoopFOR)){
                    if(p.parent == null)
                        throw new ScriptException(this.line,"No loop to break.");
                    p = p.parent;
                }
                ((ScriptLoop) p).doBreak();
                return;
            case 3:
                TypeNumber n = (TypeNumber) getParameters().get(0).get(context);
                p = this;
                for (int i = 0; i < n.getObject(); i++) {
                    p = p.parent;
                    while(!(p instanceof ScriptLoop.ScriptLoopWHILE) && !(p instanceof ScriptLoop.ScriptLoopFOR)){
                        if(p.parent == null)
                            throw new ScriptException(this.line,"No more loop to break.");
                        p = p.parent;
                    }
                    ((ScriptLoop) p).doBreak();
                }
                return;
        }
    }


    //We definitely stop the current execution to return the context
    @Override
    public IScript getNext(ScriptContext context) {
        //If "return" or "cancel event"
        switch(getMatchedIndex()){
            case 0: case 1:
                return null;
        }

        //Else if "break"
        return super.getNext(context);
    }

    @Override
    public IScript getParent() {
        //If "return" or "cancel event"
        switch(getMatchedIndex()){
            case 0: case 1:
                return null;
        }

        //Else if "break"
        return super.getParent();
    }
}
