package fr.nico.sqript.events;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.*;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtPlayer {

    @Cancelable
    @Event(
            feature = @Feature(name = "Player movement",
                    description = "Called when a player moves.",
                    examples = "on player movement:\n" + "    cancel event #Freezes the player",
                    pattern = "player move[ment]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that moved.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnPlayerMove extends ScriptEvent {

        public EvtOnPlayerMove(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Item right clicked",
                    description = "Called when a player right clicks an item.",
                    examples = "on item click with minecraft:emerald:",
                    pattern = "[item] click [with {item}] [with ((1;left)|(2;right)) hand]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that clicked on the item.", pattern = "player", type = "player"),
                    @Feature(name = "Clicked item", description = "The clicked item.", pattern = "[click[ed]] item", type = "item"),
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
    @Event(
            feature = @Feature(name = "Message sent",
                    description = "Called when a player sends a message.",
                    examples = "on message sent:\n" + "    set message to \"My message\" #Edit message content",
                    pattern = "([player] sen(d[ing]|t) [a] message|message sent)"),
            accessors = {
                    @Feature(name = "Sender", description = "The sender of this message.", pattern = "(player|sender)", type = "player"),
                    @Feature(name = "Message", description = "The content of the message.", pattern = "message", type = "string"),
            }
    )
    public static class EvtOnPlayerSendMessage extends ScriptEvent {

        public EvtOnPlayerSendMessage(EntityPlayer player, String message) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "player"),
                    new ScriptTypeAccessor(new TypeString(message), "message"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Item pickup",
                    description = "Called when a player pickups an item",
                    examples = "on item pickup:\n" +
                            "    if item is minecraft:bedrock:\n" +
                            "        cancel event #Prevents bedrock pickup.",
                    pattern = "(player pickup[s] item|item pickup)"),
            accessors = {
                    @Feature(name = "Player", description = "The player that picked up the items.", pattern = "player", type = "player"),
                    @Feature(name = "Picked up item", description = "The picked up item.", pattern = "[picked [up]] item", type = "item"),
            }
    )
    public static class EvtOnItemPickup extends ScriptEvent {

        public EvtOnItemPickup(EntityPlayer player,ItemStack item) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),
                    new ScriptTypeAccessor(new TypeItem(item),"[picked [up]] item"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Item use",
                    description = "Called when a player uses an item",
                    examples = "on item use:\n" +
                            "    if item is minecraft:potion:\n" +
                            "    cancel event",
                    pattern = "(player use[s] item|item use)"),
            accessors = {
                    @Feature(name = "Player", description = "The player that used the item.", pattern = "player", type = "player"),
                    @Feature(name = "Used item", description = "The used item.", pattern = "[used] item", type = "item"),
            }
    )
    public static class EvtOnItemUse extends ScriptEvent {

        public EvtOnItemUse(EntityPlayer player,ItemStack item) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),
                    new ScriptTypeAccessor(new TypeItem(item),"[used] item"));
        }

    }



    @Cancelable
    @Event(
            feature = @Feature(name = "Player attack",
                    description = "Called when a player is hit by another player.",
                    examples = "on player attacked:\n" +
                            "    cancel event #Removes pvp",
                    pattern = "player (hit|attacked)"),
            accessors = {
                    @Feature(name = "Attacker",description = "The player that attacked.", pattern = "attacker", type = "player"),
                    @Feature(name = "Victim",description = "The player that was attacked.", pattern = "victim", type = "player"),
            }
    )
    public static class EvtOnPlayerHit extends ScriptEvent {

        public EvtOnPlayerHit(EntityPlayer victim,EntityPlayer attacker) {
            super(new ScriptTypeAccessor(new TypePlayer(victim),"victim"),
                    new ScriptTypeAccessor(new TypePlayer(attacker),"attacker"));
        }

    }




    @Cancelable
    @Event(
            feature = @Feature(name = "Player jump",
                    description = "Called when a player jumps.",
                    examples = "on player jump:\n" +
                            "    cancel event #Forbid jumping",
                    pattern = "[player] (jump|jumped)"),
            accessors = {
                    @Feature(name = "Player",description = "The player that jumped.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnPlayerJump extends ScriptEvent {

        public EvtOnPlayerJump(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }


    @Cancelable
    @Event(
            feature = @Feature(name = "Player login",
                    description = "Called when a player logs in.",
                    examples = "on player login:",
                    pattern = "[player] (login|connection)"),
            accessors = {
                    @Feature(name = "Player",description = "The player that logged in.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnPlayerLogin extends ScriptEvent {

        public EvtOnPlayerLogin(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Entity interact",
                    description = "This event is triggered when a player interacts with an entity (right-click).",
                    examples = "on right click on living entity:",
                    pattern = "right click on living entity"),
            accessors = {
                    @Feature(name = "Interaction entity",description = "The entity that has been interacted with.", pattern = "target", type = "entity"),
                    @Feature(name = "Interaction hand",description = "The hand that has been used to interact.", pattern = "hand", type = "hand"),
                    @Feature(name = "Player",description = "The player that interacted with the entity.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnEntityInteract extends ScriptEvent {

        public EvtOnEntityInteract(Entity entity, EnumHand hand, EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypeEntity(entity),"target"), new ScriptTypeAccessor(new TypeHand(hand),"hand"), new ScriptTypeAccessor(new TypePlayer(player), "player"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Player death",
                    description = "This event is triggered just before a player dies of damage.",
                    examples = "on player death:",
                    pattern = "[player] death"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the death event.", pattern = "victim", type = "player"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
            }
    )
    public static class EvtOnLivingDeath extends ScriptEvent {

        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Player death drops",
                    description = "This event is triggered when the death of an player causes the appearance of objects.",
                    examples = "on drop of player death:",
                    pattern = "drop[s] of player death"),
            accessors = {
                    @Feature(name = "Player", description = "The victim of the death event.", pattern = "victim", type = "player"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
                    @Feature(name = "Drops", description = "An array of the items that are going to be dropped.", pattern = "drops", type = "array"),
            }
    )
    public static class EvtOnPlayerDrops extends ScriptEvent {

        public EvtOnPlayerDrops(EntityPlayer player, DamageSource damageSource, TypeArray typeArray) {
            super(new ScriptTypeAccessor(player != null ? new TypePlayer(player) : new TypeNull(),"player"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptTypeAccessor(typeArray,"drops"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Player drop",
                    description = "Event that is fired whenever a player tosses (Q) an item or drag-n-drops a stack of items outside the inventory GUI screens.",
                    examples = "on player drop:",
                    pattern = "[player] drop[ing]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that dropped the item.", pattern = "player", type = "player"),
                    @Feature(name = "Item", description = "The item that has been tossed.", pattern = "item", type = "item")
            }
    )
    public static class EvtOnItemToss extends ScriptEvent {

        public EvtOnItemToss(EntityPlayer entityPlayer, EntityItem itemEntity) {
            super(new ScriptTypeAccessor(entityPlayer != null ? new TypePlayer(entityPlayer) : new TypeNull(),"player"), new ScriptTypeAccessor(new TypeItem(itemEntity.getItem()),"item"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Key input",
                    description = "Event that is fired whenever a registered key input is triggered.",
                    examples = "on key input:",
                    pattern = "key input"),
            accessors = {}
    )
    public static class EvtOnKeyInputEvent extends ScriptEvent {

        public EvtOnKeyInputEvent() {
            super();
        }

    }

    @Event(
            feature = @Feature(name = "Player respawn",
                    description = "This event is triggered when a player reappears in the world after dying or passing through the end portal to the overworld.",
                    examples = "on respawn:",
                    pattern = "[player] respawn[ing]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that respawned.", pattern = "player", type = "player"),
                    @Feature(name = "End conquered", description = "Whether the respawn is due to the end conquest.", pattern = "end conquered", type = "boolean")
            }
    )
    public static class EvtOnPlayerRespawnEvent extends ScriptEvent {

        public EvtOnPlayerRespawnEvent(EntityPlayer entityPlayer, boolean endConquered) {
            super(new ScriptTypeAccessor(entityPlayer != null ? new TypePlayer(entityPlayer) : new TypeNull(),"player"), new ScriptTypeAccessor(new TypeBoolean(endConquered),"endConquered"));
        }

    }
}
