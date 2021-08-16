package fr.nico.sqript.events;

import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtPlayer {

    @Cancelable
    @Event(name = "Player movement",
            description = "Called when a player move",
            examples = "on player movement:",
            patterns = "player move[ment]",
            accessors = "player:player")
    public static class EvtOnPlayerMove extends ScriptEvent {

        public EvtOnPlayerMove(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(name = "Item right clicked",
            description = "Called when a player right clicks an item",
            examples = "on click on stick:",
            patterns = "[item] click [with {item}] [with ((1;left)|(2;right)) hand]",
            accessors = {"player:player","[click[ed]] item:item"
            }
    )
    public static class EvtOnItemRightClick extends ScriptEvent {

        public ItemStack clickedItem;
        public EnumHand hand;

        public EvtOnItemRightClick(EntityPlayer player, ItemStack clicked, EnumHand hand) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(new TypeItem(clicked),"[click[ed]] item"));
            this.clickedItem = clicked;
            this.hand = hand;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            boolean hand = true;
            if (this.hand == EnumHand.MAIN_HAND)
                hand = ((marks >> 2) & 1)==1;

            if (this.hand == EnumHand.OFF_HAND)
                hand = ((marks >> 1) & 1)==1;

            hand = hand | marks == 0; //Case in which no hand has been configured

            if(parameters.length==0 || parameters[0] == null)
                return hand;

            return (((TypeResource)parameters[0]).getObject().equals(clickedItem.getItem().getRegistryName())) && hand;
        }
    }


    @Cancelable
    @Event(name = "message sent",
            description = "Called when a player sends a message",
            examples = "on player sending message:",
            patterns = "(([player] sen(d[ing]|t) [a] message|message sent))",
            accessors = {"(player|sender):player","message:string"}
    )
    public static class EvtOnPlayerSendMessage extends ScriptEvent {

        public EvtOnPlayerSendMessage(EntityPlayer player, String message) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "player"),
                    new ScriptTypeAccessor(new TypeString(message), "message"));
        }
    }

    @Cancelable
    @Event(name = "Item pickup",
            description = "Called when a player pickups an item",
            examples = "on item pickup:",
            patterns = "(player pickup[s] item|item pickup)",
            accessors = {"player:player","[picked [up]] item:item"
            }
    )
    public static class EvtOnItemPickup extends ScriptEvent {

        public EvtOnItemPickup(EntityPlayer player,ItemStack item) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),
                    new ScriptTypeAccessor(new TypeItem(item),"[picked [up]] item"));
        }

    }

    @Cancelable
    @Event(name = "Item use",
            description = "Called when a player uses an item",
            examples = "on item use:",
            patterns = "(player use[s] item|item use)",
            accessors = {"player:player","[used] item:item"
            }
    )
    public static class EvtOnItemUse extends ScriptEvent {

        public EvtOnItemUse(EntityPlayer player,ItemStack item) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),
                    new ScriptTypeAccessor(new TypeItem(item),"[used] item"));
        }

    }



    @Cancelable
    @Event(name = "Player attack",
            description = "Called when a player is hit by another player",
            examples = "on player hit:",
            patterns = "player (hit|attacked)",
            accessors = {"attacker:player","victim:player"
            }
    )
    public static class EvtOnPlayerHit extends ScriptEvent {

        public EvtOnPlayerHit(EntityPlayer victim,EntityPlayer attacker) {
            super(new ScriptTypeAccessor(new TypePlayer(victim),"victim"),
                    new ScriptTypeAccessor(new TypePlayer(attacker),"attacker"));
        }

    }




    @Cancelable
    @Event(name = "Player jump",
            description = "Called when a player jumps",
            examples = "on player jump:",
            patterns = "[player] (jump|jumped)",
            accessors = {"player:player"}
    )
    public static class EvtOnPlayerJump extends ScriptEvent {

        public EvtOnPlayerJump(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }


    @Cancelable
    @Event(name = "Player login",
            description = "Called when a player logs in",
            examples = "on player login:",
            patterns = "player (login|connection)",
            accessors = {"player:player"}
    )
    public static class EvtOnPlayerLogin extends ScriptEvent {

        public EvtOnPlayerLogin(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }


}
