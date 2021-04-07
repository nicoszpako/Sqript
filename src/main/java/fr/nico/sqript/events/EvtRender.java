package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypePlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtRender {

    @Event(name = "render of overlay",
            description = "Called when overlay is rendered",
            examples = "on render overlay:",
            patterns = "render [of] overlay",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderOverlay extends ScriptEvent {

        public EvtOnRenderOverlay(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Event(name = "render of crosshair",
            description = "Called when crosshair is rendered",
            examples = "on render of crosshair:",
            patterns = "render [of] crosshair",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderCrosshair extends ScriptEvent {

        public EvtOnRenderCrosshair(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Event(name = "render of experience bar",
            description = "Called when experience bar is rendered",
            examples = "on render xp bar:",
            patterns = "render [of] (xp|experience) bar",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderXPBar extends ScriptEvent {

        public EvtOnRenderXPBar(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(name = "render of chat",
            description = "Called when chat is rendered",
            examples = "on render chat:",
            patterns = "render [of] chat",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderChat extends ScriptEvent {


        public EvtOnRenderChat(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(name = "render of food bar",
            description = "Called when food bar is rendered",
            examples = "on render food bar:",
            patterns = "render [of] food [bar]",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderFoodBar extends ScriptEvent {


        public EvtOnRenderFoodBar(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(name = "render of health bar",
            description = "Called when health bar is rendered",
            examples = "on render health bar:",
            patterns = "render [of] health [bar]",
            accessors = {"player:player"},
            side = Side.CLIENT)
    public static class EvtOnRenderHealthBar extends ScriptEvent {


        public EvtOnRenderHealthBar(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(name = "render of nameplates",
            description = "Called when nameplates are rendered",
            examples = "on render of player's nameplates:",
            patterns = "render [of] player['s] nameplates",
            accessors = {"player:player"})
    public static class EvtOnDrawNameplate extends ScriptEvent {

        public EvtOnDrawNameplate(EntityPlayer player) {
            super(new ScriptAccessor(new TypePlayer(player),"player"));
        }

    }


}
