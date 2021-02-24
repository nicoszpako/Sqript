package fr.nico.sqript.events;

import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerEvents{

    @Event(name = "Player movement",
            description = "Called when a player move",
            examples = "on player movement:",
            patterns = "player move[ment]",
            accessors = "player:player")
    public static class EvtOnPlayerMove extends ScriptEvent {

        public EvtOnPlayerMove(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Event(name = "Player attacked",
            description = "Called when a player is hit by another player",
            examples = "on player hit:",
            patterns = "player (hit|attacked)",
            accessors = {"attacker:player","victim:player"
            }
    )
    public static class EvtOnPlayerHit extends ScriptEvent {

        public EvtOnPlayerHit(EntityPlayer victim,EntityPlayer attacker) {
            super(new ScriptAccessor(new TypePlayer(victim),"victim"),
                    new ScriptAccessor(new TypePlayer(attacker),"attacker"));
        }

    }

    @Event(name = "Player jump",
            description = "Called when a player jumps",
            examples = "on player jump:",
            patterns = "player (jump|jumped)",
            accessors = {"player:player"}
    )
    public static class EvtOnPlayerJump extends ScriptEvent {

        public EvtOnPlayerJump(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Event(name = "Player login",
            description = "Called when a player logs in",
            examples = "on player login:",
            patterns = "player (login|connection)",
            accessors = {"player:player"}
    )
    public static class EvtOnPlayerLogin extends ScriptEvent {

        public EvtOnPlayerLogin(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }


}
