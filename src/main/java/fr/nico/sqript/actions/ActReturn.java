package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;

@Action(name = "Return/break Actions",
        features = {
        @Feature(name = "Return", description = "In a function, returns a value to be used as a result of the function", examples = "function plusOne(n):\n    return n + 1", pattern = "return {element}"),
        @Feature(name = "Cancel event", description = "In an event, cancels it.", examples = "on player movement:\n    cancel event", pattern = "cancel event"),
        @Feature(name = "Break loop", description = "In a loop, interrupts it and exit it.", examples = "for {i} in numbers in range of 10:\n   if {i} = 9:\n      break loop",pattern = "break loop"),
        @Feature(name = "Break multiple loops", description = "In multiple loop, interrupts a specific number of loops and exit them.", examples = "for {i} in numbers in range of 10:\n   for {j} in numbers in range of 10:\n      if {j} = 9:\n         break 2 loops",pattern = "break loops"),
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
                //System.out.println("Setting return value to true : ");
                //System.out.println("Context return are "+context.getReturnValue());
                context.setReturnValue(TypeBoolean.TRUE());
                //System.out.println("Context return are "+context.getReturnValue());
                return;
            case 2:
                IScript p = super.parent;
                while(!(p instanceof ScriptLoop.ScriptLoopRepeated)){
                    if(p.parent == null)
                        throw new ScriptException(getLine(),"No loop to break.");
                    p = p.parent;
                }
                ((ScriptLoop.ScriptLoopRepeated) p).doBreak();
                return;
            case 3:
                TypeNumber n = (TypeNumber) getParameters().get(0).get(context);
                p = this;
                for (int i = 0; i < n.getObject(); i++) {
                    p = p.parent;
                    while(!(p instanceof ScriptLoop.ScriptLoopRepeated)){
                        if(p.parent == null)
                            throw new ScriptException(getLine(),"No more loop to break.");
                        p = p.parent;
                    }
                    ((ScriptLoop.ScriptLoopRepeated) p).doBreak();
                }
                return;
        }
    }


    //We definitely stop the current execution to return the context
    @Override
    public IScript getNext(ScriptContext context) throws ScriptException {
        //"return"
        if (getMatchedIndex() == 0) {
            return null;
        }
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
