package fr.nico.sqript.events;

import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import net.minecraft.entity.player.EntityPlayer;

@Event(name = "Player attacks",
        description = "Called when a player is hit by another player",
        examples = "on player hit:",
        patterns = "player (hit|attacked)",
        accessors = {"attacker:player","victim:player"
}
)
public class EvtOnPlayerHit extends ScriptEvent {

    public EvtOnPlayerHit(EntityPlayer victim,EntityPlayer attacker) {
        super(new ScriptAccessor(new TypePlayer(victim),"victim"),
                new ScriptAccessor(new TypePlayer(attacker),"attacker"));
    }

}
