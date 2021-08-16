package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;

@Event(name = "window setup",
        description = "Called only once when window is set up",
        examples = "on window setup:",
        patterns = "window setup",
        accessors = {})
public class EvtOnWindowSetup extends ScriptEvent {
    public EvtOnWindowSetup() {
        super();
    }

}
