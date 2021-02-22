package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.blocks.ScriptBlockEvent;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.types.primitive.TypeBoolean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScriptInstance {

    private final Map<String, ScriptExpression> options = new HashMap<>();

    private final List<ScriptBlock> blocks = new ArrayList<>();
    private final File scriptFile;
    private final String name;

    public ScriptInstance(String name, File scriptFile) {
        this.name=name;
        this.scriptFile = scriptFile;
    }

    public File getScriptFile() {
        return scriptFile;
    }


    public Map<String, ScriptExpression> getOptions() {
        return options;
    }

    public boolean callEvent(ScriptContext context, ScriptEvent event) {
        //System.out.println("Calling event : "+event.getClass());
        return (Boolean) callEventAndGetContext(context,event).returnValue.element.getObject();
    }

    public List<ScriptBlock> getBlocksOfClass(Class<? extends ScriptBlock> type){
        return blocks.stream().filter(a->a.getClass().isAssignableFrom(type)).collect(Collectors.toList());
    }

    public ScriptFunctionalBlock getFunction(String name){
        for (ScriptBlock f : blocks) {
            if(f instanceof ScriptFunctionalBlock){
                ScriptFunctionalBlock function = (ScriptFunctionalBlock) f;
                if (function.name.equals(name))
                    return function;
            }
        }
        return null;
    }

    public ScriptContext callEventAndGetContext(ScriptContext context,ScriptEvent event) {
        //System.out.println("Trying to call event : "+event.getClass().getSimpleName());
        context.returnValue = new ScriptAccessor(TypeBoolean.FALSE(), "");
        //long t1 = System.currentTimeMillis();
        for (ScriptBlock b : getBlocksOfClass(ScriptBlockEvent.class)) {
            ScriptBlockEvent t = (ScriptBlockEvent) b;
            //System.out.println("Checking for class : "+t.eventType);
            if (t.eventType == event.getClass()) {
                //System.out.println("Calling event : "+event.getClass().getSimpleName());
                try {
                    ScriptClock clock = new ScriptClock(context);
                    context.wrap(event.accessors);
                    clock.start(t);
                } catch (Exception e) {
                    if (ScriptManager.FULL_DEBUG) e.printStackTrace();
                    ScriptManager.log.error("Error while calling event");
                    if (e instanceof ScriptException) {
                        for (String s : e.getMessage().split("\n"))
                            ScriptManager.log.error(s);
                    } else if (e instanceof ClassCastException) {
                        ScriptManager.log.error("Type error !");
                    }
                }
                //System.out.println("Finished ! It took : " + (System.currentTimeMillis() - t1) + " ms");
                return context;
            }
        }
        return context;
    }

    public String getName() {
        return name;
    }


    public void registerBlock(ScriptBlock block) {
        //System.out.println("Adding block : "+block.getClass());
        blocks.add(block);
    }
}
