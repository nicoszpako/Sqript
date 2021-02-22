package fr.nico.sqript.events;

import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import net.minecraft.entity.player.EntityPlayer;

@Event(name = "render of nameplates",
        description = "Called when nameplates are rendered",
        examples = "on render of player's nameplates:",
        patterns = "render [of] player['s] nameplates",
        accessors = "player:player")
public class EvtOnDrawNameplate extends ScriptEvent {

    public EvtOnDrawNameplate(EntityPlayer player) {
        super(new ScriptAccessor(new TypePlayer(player),"player"));
    }

}
