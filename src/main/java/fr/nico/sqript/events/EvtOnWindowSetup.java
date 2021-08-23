package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;

@Event(
        feature = @Feature(name = "Window setup",
                description = "Called once when the game window is being set up.",
                examples = "on window setup:\n" + "    set window title to \"Sqript !\"",
                pattern = "window setup"),
        accessors = {
        }
)
public class EvtOnWindowSetup extends ScriptEvent {
    public EvtOnWindowSetup() {
        super();
    }

}
