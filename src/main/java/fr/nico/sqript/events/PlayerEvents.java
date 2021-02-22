package fr.nico.sqript.events;

import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerEvents{

    @Event(name = "Player events",
            description = "Called when a player move",
            examples = "on player movement:",
            patterns = "player move[ment]",
            accessors = "player:player")
    public static class EvtOnPlayerMove extends ScriptEvent {

        public EvtOnPlayerMove(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

}
