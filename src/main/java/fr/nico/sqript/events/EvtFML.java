package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.primitive.TypeBoolean;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class EvtFML {

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

    @Event(name = "Server Starting",
            description = " This event allows for customizations of the server, such as loading custom commands, perhaps customizing recipes or other activities.",
            examples = "on server start:",
            patterns = "server start",
            accessors = {"server:console"}
    )
    public static class EvtOnServerStartingEvent extends ScriptEvent {
        public EvtOnServerStartingEvent(MinecraftServer server) {
            super(new ScriptAccessor(new TypeConsole(server),"server"));
        }
    }
}
