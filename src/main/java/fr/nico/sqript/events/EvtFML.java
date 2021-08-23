package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypeConsole;
import net.minecraft.server.MinecraftServer;

public class EvtFML {

    @Event(
            feature = @Feature(name = "Pre initialization",
                    description = "This is the first of three commonly called events during mod initialization.",
                    examples = "on pre init:",
                    pattern = "pre init"),
            accessors = {}
    )
    public static class EvtOnPreInit extends ScriptEvent {
        public EvtOnPreInit() {
            super();
        }
    }

    @Event(
            feature = @Feature(name = "Initialization",
                    description = "Called after 'on pre init:' and before 'on post init:' during mod startup.",
                    examples = "on init:",
                    pattern = "init"),
            accessors = {}
    )
    public static class EvtOnInit extends ScriptEvent {
        public EvtOnInit() {
            super();
        }
    }

    @Event(
            feature = @Feature(name = "Post initialization",
                    description = "Called after that 'on init:' has been dispatched on every mod. This is the third and last commonly called event during mod initialization.",
                    examples = "on post init:",
                    pattern = "post init"),
            accessors = {}
    )
    public static class EvtOnPostInit extends ScriptEvent {
        public EvtOnPostInit() {
            super();
        }
    }

    @Event(
            feature = @Feature(name = "Server starting",
                    description = "This event allows for customizations of the server, such as loading custom commands, perhaps customizing recipes or other activities.",
                    examples = "on server start:",
                    pattern = "server start"),
            accessors = {
                    @Feature(name = "Server", description = "The instance of the server", pattern = "server", type = "console")
            }
    )
    public static class EvtOnServerStartingEvent extends ScriptEvent {
        public EvtOnServerStartingEvent(MinecraftServer server) {
            super(new ScriptTypeAccessor(new TypeConsole(server), "server"));
        }
    }
}
