package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.ScriptTimer;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.expressions.ExprDate;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.EventDefinition;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptInstance;
import fr.nico.sqript.types.TypeDate;

import java.util.Arrays;

@Block(name = "time loop",
        description = "Time looping blocks",
        examples = "every 1 day:",
        regex = "every .*")
public class ScriptBlockTimeLoop extends ScriptBlock {

    public long delay;


    public ScriptBlockTimeLoop(ScriptLine head) {
        try{
            this.delay = getDelay(head);
        }catch(Exception e){
            if(ScriptManager.FULL_DEBUG)e.printStackTrace();
        }
    }

    public long getDelay(ScriptLine line) throws Exception {
        line.text = line.text.replaceAll("every\\s+", "").replaceAll(":", "");
        ScriptExpression expr = ScriptDecoder.getExpression(line,new ScriptCompileGroup());
        System.out.println("Loading time looping block :");
        System.out.println(expr.getClass());
        System.out.println(expr.get(new ScriptContext()).getClass());
        System.out.println(expr.get(new ScriptContext()).getObject());
        return (((Long)expr.get(new ScriptContext()).getObject()));
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        ScriptCompileGroup group = new ScriptCompileGroup();
        setRoot(scriptLineBlock.compile(group));
        ScriptTimer.loopIScript(this,delay);
    }

}