package fr.nico.sqript.events;

import fr.nico.sqript.types.*;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.ArrayList;
import java.util.List;

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
            patterns = "[item] click [with {item}] [with ((1;left)|(2;right)) hand]",
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
    @Event(name = "message sent",
            description = "Called when a player sends a message",
            examples = "on player sending message:",
            patterns = "(([player] sen(d[ing]|t) [a] message|message sent))",
            accessors = {"(player|sender):player","message:string"}
    )
    public static class EvtOnPlayerSendMessage extends ScriptEvent {

        public EvtOnPlayerSendMessage(EntityPlayer player, String message) {
            super(new ScriptAccessor(new TypePlayer(player), "player"),
                    new ScriptAccessor(new TypeString(message), "message"));
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
            super(new ScriptAccessor(new TypePlayer(player),"player"),
                    new ScriptAccessor(new TypeItem(item),"[picked [up]] item"));
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
            super(new ScriptAccessor(new TypePlayer(player),"player"),
                    new ScriptAccessor(new TypeItem(item),"[used] item"));
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
            super(new ScriptAccessor(new TypePlayer(victim),"victim"),
                    new ScriptAccessor(new TypePlayer(attacker),"attacker"));
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
            super(new ScriptAccessor(new TypePlayer(player),"player"));
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
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(name = "Entity Interact",
            description = "This event is triggered when a player interacts with an entity (right-click).",
            examples = "on right click on living entity:",
            patterns = "right click on living entity",
            accessors = {"target:entity", "hand:hand"}
    )
    public static class EvtOnEntityInteract extends ScriptEvent {

        public EvtOnEntityInteract(Entity entity, EnumHand hand) {
            super(new ScriptAccessor(new TypeEntity(entity),"target"), new ScriptAccessor(new TypeHand(hand),"hand"));
        }
    }

    @Cancelable
    @Event(name = "Player Death",
            description = "This event is triggered just before an entity dies of damage.",
            examples = "on living death:",
            patterns = "(living) death",
            accessors = {"victim:entity", "damageType:damage_source", "attacker:entity"}
    )
    public static class EvtOnLivingDeath extends ScriptEvent {
        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptAccessor(new TypeDamageSource(damageSource),"damageType"));
        }
    }

    @Cancelable
    @Event(name = "Player Drops",
            description = "This event is triggered when a player dies and is about to drop all his items on the ground.",
            examples = "on player drop of death:",
            patterns = "(player) drop[s] of death",
            accessors = {"player:player", "damageType:damage_source", "attacker:entity", "drops:array"}
    )
    public static class EvtOnPlayerDrops extends ScriptEvent {
        public EvtOnPlayerDrops(EntityPlayer player, DamageSource damageSource, TypeArray typeArray) {
            super(new ScriptAccessor(player != null ? new TypePlayer(player) : new TypeNull(),"player"), new ScriptAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptAccessor(typeArray,"drops"));
        }
    }

    @Cancelable
    @Event(name = "Player Drops",
            description = "Event that is fired whenever a player tosses (Q) an item or drag-n-drops a stack of items outside the inventory GUI screens.",
            examples = "on player drop:",
            patterns = "[player] drop[ing]",
            accessors = {"player:player", "item:item"}
    )
    public static class EvtOnItemToss extends ScriptEvent {
        public EvtOnItemToss(EntityPlayer entityPlayer, EntityItem itemEntity) {
            super(new ScriptAccessor(entityPlayer != null ? new TypePlayer(entityPlayer) : new TypeNull(),"player"), new ScriptAccessor(new TypeItem(itemEntity.getItem()),"item"));
        }
    }
}
