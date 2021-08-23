package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;

@Event(
        feature = @Feature(name = "World tick",
                description = "Called when the world ticks.",
                examples = "on world tick:",
                pattern = "world tick"),
        accessors = {
        }
)
public class EvtOnWorldTick extends ScriptEvent {

    public EvtOnWorldTick() {
    }

}
