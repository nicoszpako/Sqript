package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtFML {

    @Cancelable
    @Event(name = "Pre Initialization",
            description = "This is the first of three commonly called events during mod initialization.",
            examples = "on pre init:",
            patterns = "pre init",
            accessors = {}
    )
    public static class EvtOnPreInit extends ScriptEvent {
        public EvtOnPreInit() {
            super();
        }
    }

    @Cancelable
    @Event(name = "Initialization",
            description = "Called after on pre init: and before on post init: during mod startup.",
            examples = "on init:",
            patterns = "init",
            accessors = {}
    )
    public static class EvtOnInit extends ScriptEvent {
        public EvtOnInit() {
            super();
        }
    }

    @Cancelable
    @Event(name = "Post Initialization",
            description = "Called after on init: has been dispatched on every mod. This is the third and last commonly called event during mod initialization.",
            examples = "on post init:",
            patterns = "post init",
            accessors = {}
    )
    public static class EvtOnPostInit extends ScriptEvent {
        public EvtOnPostInit() {
            super();
        }
    }
}
