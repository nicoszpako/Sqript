package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtRender {

    @Event(
            feature = @Feature(name = "Render of overlay",
                    description = "Called when overlay is rendered",
                    examples = "on render overlay:\n" +
                            "    draw textured rectangle at [-15,-7.5] with size [30,15] using texture sample:logo.png",
                    pattern = "render [of] overlay",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderOverlay extends ScriptEvent {

        public EvtOnRenderOverlay(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

    @Event(
            feature = @Feature(name = "Render of crosshair",
                    description = "Called when crosshair is rendered",
                    examples = "on render of crosshair:",
                    pattern = "render [of] crosshair",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderCrosshair extends ScriptEvent {

        public EvtOnRenderCrosshair(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }


    @Event(
            feature = @Feature(name = "Render of experience bar",
                    description = "Called when experience bar is rendered",
                    examples = "on render xp bar:",
                    pattern = "render [of] (xp|experience) bar",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderXPBar extends ScriptEvent {

        public EvtOnRenderXPBar(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Render of chat",
                    description = "Called when chat is rendered",
                    examples = "on render chat:",
                    pattern = "render [of] chat",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderChat extends ScriptEvent {


        public EvtOnRenderChat(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Render of food bar",
                    description = "Called when food bar is rendered",
                    examples = "on render food bar:",
                    pattern = "render [of] food [bar]",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderFoodBar extends ScriptEvent {


        public EvtOnRenderFoodBar(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Render of health bar",
                    description = "Called when health bar is rendered",
                    examples = "on render of health bar:\n" +
                            "    cancel event #Hides the player's health bar",
                    pattern = "render [of] health [bar]",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnRenderHealthBar extends ScriptEvent {


        public EvtOnRenderHealthBar(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));

        }

    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Render of nameplates",
                    description = "Called when a nameplate is rendered",
                    examples = "on render of [player's] nameplate:",
                    pattern = "render [of] [player['s]] nameplate",
                    side = Side.CLIENT
            ),
            accessors = {}
    )
    public static class EvtOnDrawNameplate extends ScriptEvent {

        public EvtOnDrawNameplate(EntityPlayer player) {
            super(new ScriptTypeAccessor(new TypePlayer(player),"player"));
        }

    }


}
