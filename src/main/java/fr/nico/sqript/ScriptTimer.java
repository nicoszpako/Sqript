package fr.nico.sqript;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.ScriptClock;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ScriptTimer {

    private static long tick = 0;

    private static final ConcurrentLinkedQueue<ScriptClock> clocks = new ConcurrentLinkedQueue<>();

    public static void addDelay(ScriptClock clock, long rawDelayInTicks){
        clock.delay = tick + rawDelayInTicks;
        clocks.add(clock);
    }

    public static void tick() throws ScriptException {
        if(clocks.isEmpty())
            return;
        tick++;
        for (ScriptClock clock : clocks) {
            if (clock.delay == tick) {
                clock.resume();
            }
        }
        clocks.removeIf(c -> c.delay == tick);
    }

}
