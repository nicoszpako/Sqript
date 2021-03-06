package fr.nico.sqript.events;

import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.primitive.TypeResource;
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
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(name = "Item right clicked",
            description = "Called when a player right clicks an item",
            examples = "on click on stick:",
            patterns = "[item] click [with {item}] [with (1;left|2;right) hand]",
            accessors = {"player:player","[click[ed]] item:item"
            }
    )
    public static class EvtOnItemRightClick extends ScriptEvent {

        public ItemStack clickedItem;
        public EnumHand hand;

        public EvtOnItemRightClick(EntityPlayer player, ItemStack clicked, EnumHand hand) {
            super(new ScriptAccessor(new TypePlayer(player),"player"),new ScriptAccessor(new TypeItem(clicked),"[click[ed]] item"));
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


    @Cancelable
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
