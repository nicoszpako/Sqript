package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;

@Event(name = "world tick",
        description = "Called when the world ticks",
        examples = "on world tick:",
        patterns = "world tick",
        accessors = {})
public class EvtOnWorldTick extends ScriptEvent {

    public EvtOnWorldTick() {
    }

}
