package fr.nico.sqript;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ScriptTimer {

    private static long tick = 1;

    private static final ConcurrentLinkedQueue<ScriptClock> delayedClocks = new ConcurrentLinkedQueue<>();

    //Map (IScript : delay between each loop)
    private static final ConcurrentHashMap<IScript,Long> loopingClocks = new ConcurrentHashMap<>();

    public static void addDelay(ScriptClock clock, long rawDelayInTicks){
        clock.delay = tick + rawDelayInTicks;
        delayedClocks.add(clock);
    }

    public static void loopIScript(IScript script, long loopDelayInTicks){
        loopingClocks.put(script,loopDelayInTicks);
    }

    public static void tick() throws ScriptException {
        if(delayedClocks.isEmpty() && loopingClocks.isEmpty())
            return;

        tick++;
        for (ScriptClock clock : delayedClocks) {
            if (clock.delay == tick) {
                clock.resume();
            }
        }
        delayedClocks.removeIf(c -> c.delay == tick);

        for (IScript script : loopingClocks.keySet()) {
            if (tick % (loopingClocks.get(script)) == 0) {
                ScriptClock clock = new ScriptClock(new ScriptContext(ScriptManager.GLOBAL_CONTEXT));
                try {
                    clock.start(script);
                }catch (Exception e){
                    ScriptManager.log.error("Error while running timer loop :");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void reload() {
        delayedClocks.clear();
        loopingClocks.clear();
    }
}
