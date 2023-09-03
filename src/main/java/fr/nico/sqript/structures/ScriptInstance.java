package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptFunctionalBlock;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.blocks.ScriptBlockEvent;
import fr.nico.sqript.events.EvtBlock;
import fr.nico.sqript.events.EvtOnScriptLoad;
import fr.nico.sqript.events.EvtPlayer;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.types.primitive.TypeBoolean;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptInstance {

    private final Map<String, ScriptExpression> options = new HashMap<>();

    private final List<ScriptBlock> blocks = new ArrayList<>();
    private final File scriptFile;
    private final String name;

    public ScriptInstance(String name, File scriptFile) {
        this.name = name;
        this.scriptFile = scriptFile;
    }

    public File getScriptFile() {
        return scriptFile;
    }


    public Map<String, ScriptExpression> getOptions() {
        return options;
    }

    public boolean callEvent(ScriptContext context, ScriptEvent event) {

        try {
            context.setReturnValue(new ScriptTypeAccessor(TypeBoolean.FALSE(), ""));
            ScriptContext returnContext = callEventAndGetContext(context, event);
            return (boolean) returnContext.getReturnValue().element.getObject();
        } catch (Exception e) {
            ScriptManager.log.error("Error while calling event : " + event.getClass().getSimpleName());
            e.printStackTrace();
            if (e instanceof ScriptException.ScriptWrappedException) {
                Throwable ex = ((ScriptException.ScriptWrappedException) (e)).getWrapped();
                ScriptManager.log.error(((ScriptException.ScriptWrappedException) (e)).getLine() + " : " + ex.getMessage());
                e.printStackTrace();
            } else if (e instanceof ScriptException) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
                for (String s : e.getMessage().split("\n"))
                    ScriptManager.log.error(s);
            }
        }
        return false;
    }

    public List<ScriptBlock> getBlocksOfClass(Class<? extends ScriptBlock> type) {
        return blocks.stream().filter(a -> a.getClass().isAssignableFrom(type)).collect(Collectors.toList());
    }

    public ScriptFunctionalBlock getFunction(String name) {
        //System.out.println("Getting function for : "+name);
        for (ScriptBlock f : blocks) {
            if (f instanceof ScriptFunctionalBlock) {
                //System.out.println("- "+((ScriptFunctionalBlock) f).name +  " " +f.getHead()+" "+f);
                ScriptFunctionalBlock function = (ScriptFunctionalBlock) f;
                //System.out.println("Comparing "+name+" "+((ScriptFunctionalBlock) f).name+" "+function.name.equals(name));
                if (function.name.equals(name)) {
                    //System.out.println("Returning "+function);
                    return function;
                }
            }
        }
        return null;
    }

    public ScriptContext callEventAndGetContext(ScriptContext context, ScriptEvent event) throws ScriptException {
        //long t1 = //System.currentTimeMillis();
        //System.out.println("Calling event and get context");

        for (ScriptBlock b : getBlocksOfClass(ScriptBlockEvent.class)) {
            ScriptBlockEvent scriptBlockEvent = (ScriptBlockEvent) b;
            if(event instanceof EvtBlock.EvtOnBlockBreak){
                //System.out.println(scriptBlockEvent.eventType+" "+event.getClass());
            }
            if (scriptBlockEvent.eventType == event.getClass() && event.check(scriptBlockEvent.getParameters(), scriptBlockEvent.getMarks()) && scriptBlockEvent.side.isEffectivelyValid()) {
                //System.out.println("Calling event : "+event.getClass().getSimpleName()+" with accessors "+ Arrays.toString(event.getAccessors()));
                ScriptClock clock = new ScriptClock(context);
                context.wrap(event.getAccessors());
                clock.start(scriptBlockEvent);
                //System.out.println("Finished ! It took : " + (System.currentTimeMillis()) + " ms");
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

    public List<ScriptBlock> getBlocks() {
        return blocks;
    }

}
