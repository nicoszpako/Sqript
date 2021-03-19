package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.TypeFile;

import java.io.File;

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
