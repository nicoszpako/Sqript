package fr.nico.sqript.events;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlockEvent;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.*;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
            feature = @Feature(name = "Item left clicked",
                    description = "Called when a player left clicks an item. This event only fires on client side",
                    examples = "on item left click with minecraft:emerald:",
                    pattern = "[item] left click [with {itemtype}] [with ((1;left)|(2;right)) hand]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that clicked on the item.", pattern = "player", type = "player"),
                    @Feature(name = "Clicked item", description = "The clicked item.", pattern = "[click[ed]] item", type = "item"),
            }
    )
    public static class EvtOnItemLeftClick extends ScriptEvent {

        public ItemStack clickedItem;
        public EnumHand hand;
        public net.minecraftforge.fml.relauncher.Side side;

        public EvtOnItemLeftClick(EntityPlayer player, ItemStack clicked, EnumHand hand, net.minecraftforge.fml.relauncher.Side side) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(new TypeItemStack(clicked),"[click[ed]] item"));
            //System.out.println(ScriptManager.scripts.stream().map(a->a.getBlocks()).collect(Collectors.toList()));
            //System.out.println(ScriptManager.scripts.stream().map(a->a.getName()).collect(Collectors.toList()));
            this.clickedItem = clicked;
            this.hand = hand;
            this.side = side;
        }

        @Override
        public boolean validate(ScriptType[] parameters, int marks) {
            return (parameters[0] != null && parameters[0].getObject() != null);
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            boolean correctHands = true;
            if (checkMark(2,marks))
                correctHands = hand == EnumHand.MAIN_HAND;

            else if (checkMark(1,marks))
                correctHands = hand == EnumHand.OFF_HAND;

            if(parameters.length==0 || parameters[0] == null)
                return correctHands;

            return correctHands && ((ScriptManager.parse(parameters[0],TypeItem.class)).getObject().equals(clickedItem.getItem()));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Item right clicked",
                    description = "Called when a player right clicks an item.",
                    examples = "on item click with minecraft:emerald:",
                    pattern = "[((1;client)|(2;server))] [(item|right)] click [with {itemtype}] [with ((3;left)|(4;right)) hand]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that clicked on the item.", pattern = "player", type = "player"),
                    @Feature(name = "Clicked item", description = "The clicked item.", pattern = "[click[ed]] item", type = "item"),
            }
    )
    public static class EvtOnItemRightClick extends ScriptEvent {

        public ItemStack clickedItem;
        public EnumHand hand;
        public net.minecraftforge.fml.relauncher.Side side;

        public EvtOnItemRightClick(EntityPlayer player, ItemStack clicked, EnumHand hand, net.minecraftforge.fml.relauncher.Side side) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),new ScriptTypeAccessor(new TypeItemStack(clicked),"[click[ed]] item"));
            //System.out.println(ScriptManager.scripts.stream().map(a->a.getBlocks()).collect(Collectors.toList()));
            //System.out.println(ScriptManager.scripts.stream().map(a->a.getName()).collect(Collectors.toList()));
            this.clickedItem = clicked;
            this.hand = hand;
            this.side = side;
        }

        @Override
        public boolean validate(ScriptType[] parameters, int marks) {
            return (parameters[0] != null && parameters[0].getObject() != null);
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            //System.out.println(side+" "+hand+" "+checkMark(2,marks)+" "+checkMark(1,marks)+" "+checkMark(4,marks)+" "+parameters[0]+" "+clickedItem+" "+((ScriptManager.parse(parameters[0],TypeItem.class)).getObject().equals(clickedItem.getItem())));

            boolean correctSide = true;
            if(checkMark(2,marks))
                correctSide = side.isServer();
            if(checkMark(1,marks))
                correctSide = side.isClient();

            boolean correctHands = true;
            if (checkMark(4,marks))
                correctHands = hand == EnumHand.MAIN_HAND;

            else if (checkMark(3,marks))
                correctHands = hand == EnumHand.OFF_HAND;

            if(parameters.length==0 || parameters[0] == null)
                return correctHands;

            return correctSide && correctHands && ((ScriptManager.parse(parameters[0],TypeItem.class)).getObject().equals(clickedItem.getItem()));
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
            super(new ScriptTypeAccessor(new TypePlayer(player), "(player|sender)"),
                    new ScriptTypeAccessor(new TypeString(message), "message"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Command sent",
                    description = "Called when a player sends a command.",
                    examples = "on command sent:\n" + "    set command to \"/help\" #Edit command content",
                    pattern = "([player] sen(d[ing]|(t|s)) [a] command|command sent)"),
            accessors = {
                    @Feature(name = "Sender", description = "The sender of this command.", pattern = "(player|sender)", type = "player"),
                    @Feature(name = "Command parameters", description = "The sent parameters as a string array.", pattern = "(arguments|parameters)", type = "array"),
                    @Feature(name = "Command name", description = "The command name.", pattern = "command name", type = "string"),
            }
    )
    public static class EvtOnPlayerSendCommand extends ScriptEvent {

        public EvtOnPlayerSendCommand(EntityPlayer player, String commandName, String[] arguments) {
            super(new ScriptTypeAccessor(new TypePlayer(player), "(player|sender)"),
                    new ScriptTypeAccessor(new TypeArray(new ArrayList<>(Arrays.stream(arguments).map(a->new TypeString(a)).collect(Collectors.toList()))), "(arguments|parameters)"),
                    new ScriptTypeAccessor(new TypeString(commandName), "command name"));
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
                    new ScriptTypeAccessor(new TypeItemStack(item),"[picked [up]] item"));
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
                    new ScriptTypeAccessor(new TypeItemStack(item),"[used] item"));
        }

    }

    @Event(
            feature = @Feature(name = "Item used",
                    description = "Called when a player has finished using an item",
                    examples = "on item used:\n" +
                            "    if item is minecraft:potion:\n",
                    pattern = "(player used item|item used)"),
            accessors = {
                    @Feature(name = "Player", description = "The player that used the item.", pattern = "player", type = "player"),
                    @Feature(name = "Used item", description = "The used item.", pattern = "[used] item", type = "item"),
            }
    )
    public static class EvtOnItemUsed extends ScriptEvent {

        public EvtOnItemUsed(EntityPlayer player,ItemStack item) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"),
                    new ScriptTypeAccessor(new TypeItemStack(item),"[used] item"));
        }

    }




    @Cancelable
    @Event(
            feature = @Feature(name = "Player hit",
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
            feature = @Feature(name = "Player attack",
                    description = "Called when a player attacks another entity.",
                    examples = "on player attacking minecraft:pig :\n" +
                            "    cancel event #Removes pvp",
                    pattern = "player attack[ing] [{entity|resource}]"),
            accessors = {
                    @Feature(name = "Attacker",description = "The player that attacked.", pattern = "attacker", type = "player"),
                    @Feature(name = "Victim",description = "The entity that was attacked.", pattern = "victim", type = "entity"),
            }
    )
    public static class EvtOnPlayerAttack extends ScriptEvent {

        Entity victim;

        public EvtOnPlayerAttack(Entity victim,EntityPlayer attacker) {
            super(new ScriptTypeAccessor(new TypeEntity(victim),"victim"),
                    new ScriptTypeAccessor(new TypePlayer(attacker),"attacker"));
            this.victim = victim;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            if (parameters.length == 0 || parameters[0] == null)
                return true;
            else if (parameters[0] instanceof TypeEntity) {
                return victim.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == victim.getClass();
            }
            return false;
        }

    }


    @Cancelable
    @Event(
            feature = @Feature(name = "Player jump",
                    description = "Called when a player jumps.",
                    examples = "on player jump:\n" +
                            "    cancel event #Forbid jumping",
                    pattern = "[player] (jump|jumped)", side = Side.CLIENT),
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
                    examples = "on right click on villager:",
                    pattern = "[right] click on ((1;(living|entity))|(2;{entity|resource})) [entity]"),
            accessors = {
                    @Feature(name = "Interaction entity",description = "The entity that has been interacted with.", pattern = "(target|entity)", type = "entity"),
                    @Feature(name = "Interaction hand",description = "The hand that has been used to interact.", pattern = "hand", type = "hand"),
                    @Feature(name = "Player",description = "The player that interacted with the entity.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnEntityInteract extends ScriptEvent {

        Entity entity;

        public EvtOnEntityInteract(Entity entity, EnumHand hand, EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypeEntity(entity),"(target|entity)"),
                    new ScriptTypeAccessor(new TypeHand(hand),"hand"),
                    new ScriptTypeAccessor(new TypePlayer(player), "player"));
            this.entity = entity;
        }

        @Override
        public boolean validate(ScriptType[] parameters, int marks) {
            //System.out.println("Validating with : "+ Arrays.toString(parameters)+" "+(parameters[0]==null));
            if (parameters[0] != null){
                if(parameters[0] instanceof TypeResource)
                    return ForgeRegistries.ENTITIES.getValue((ResourceLocation) parameters[0].getObject()) != null;
                else return parameters[0] instanceof TypeEntity
                        || parameters[0] instanceof TypeNull;
            }
            return false;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            if (parameters.length == 0 || parameters[0] == null)
                return true;
            else if (parameters[0] instanceof TypeEntity) {
                return entity.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == entity.getClass();
            }
            return false;
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
            super(new ScriptTypeAccessor(entityPlayer != null ? new TypePlayer(entityPlayer) : new TypeNull(),"player"), new ScriptTypeAccessor(new TypeItemStack(itemEntity.getItem()),"item"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Key input",
                    description = "Event that is fired whenever a registered key input is triggered.",
                    examples = "on key input:",
                    pattern = "key input",
                    side = Side.CLIENT),
            accessors = {}
    )
    public static class EvtOnKeyInputEvent extends ScriptEvent {
        public EvtOnKeyInputEvent() {
            super(new ScriptTypeAccessor());
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Mouse input",
                    description = "Event that is fired whenever a mouse input is triggered.",
                    examples = "on mouse input:",
                    pattern = "mouse input",
                    side = Side.CLIENT),
            accessors = {}
    )
    public static class EvtOnMouseInputEvent extends ScriptEvent {
        public EvtOnMouseInputEvent() {
            super(new ScriptTypeAccessor());
        }
    }

    @Event(
            feature = @Feature(name = "Player respawn",
                    description = "This event is triggered when a player reappears in the world after dying or passing through the end portal to the overworld.",
                    examples = "on respawn:",
                    pattern = "[player] [re]spawn[ing]"),
            accessors = {
                    @Feature(name = "Player", description = "The player that respawned.", pattern = "player", type = "player"),
                    @Feature(name = "End conquered", description = "Whether the respawn is due to the end conquest.", pattern = "end conquered", type = "boolean")
            }
    )
    public static class EvtOnPlayerRespawn extends ScriptEvent {

        public EvtOnPlayerRespawn(EntityPlayer entityPlayer, boolean endConquered) {
            super(new ScriptTypeAccessor(entityPlayer != null ? new TypePlayer(entityPlayer) : new TypeNull(),"player"), new ScriptTypeAccessor(new TypeBoolean(endConquered),"endConquered"));
        }

    }

    @Event(
            feature = @Feature(name = "Player tick",
                    description = "Called when the player ticks.",
                    examples = "on player tick:",
                    pattern = "player tick"),
            accessors = {
                    @Feature(name = "Player", description = "The entity player.", pattern = "player", type = "player"),
            }
    )
    public static class EvtOnPlayerTick extends ScriptEvent {

        public EvtOnPlayerTick(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

}
