package fr.nico.sqript.events;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.ArrayList;
import java.util.List;

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
            feature = @Feature(name = "Render of player",
                    description = "Called when player is rendered",
                    examples = "on player render:\n" +
                            "    set left arm rotation to [90,0,0]",
                    pattern = "(render[ing] [of] player|player['s] render[ing])",
                    side = Side.CLIENT
            ),
            accessors = {
                    @Feature(name = "Player",description = "The player being rendered",type = "player",pattern = "player"),
                    @Feature(name = "Limb swing",description = "The model's limb swing",type = "number",pattern = "limb swing"),
                    @Feature(name = "Limb swing amount",description = "The model's limb swing amount",type = "number",pattern = "limb swing amount"),
                    @Feature(name = "Head yaw",description = "The model's head yaw",type = "number",pattern = "head yaw"),
                    @Feature(name = "Head Pitch",description = "The model's head pitch",type = "number",pattern = "head pitch"),
                    @Feature(name = "Partial ticks",description = "The current partial ticks",type = "number",pattern = "partial ticks"),
                    @Feature(name = "Left arm rotation",description = "The player's left arm rotation",type = "array",pattern = "left arm rotation"),
                    @Feature(name = "Left arm position",description = "The player's left arm position",type = "array",pattern = "left arm position"),
                    @Feature(name = "Left arm anchor",description = "The player's left arm rotation anchor (or rotation origin, i.e the point that the part will be rotated around)",type = "array",pattern = "left arm anchor"),
                    @Feature(name = "Right arm rotation",description = "The player's right arm rotation",type = "array",pattern = "right arm rotation"),
                    @Feature(name = "Right arm position",description = "The player's right arm position",type = "array",pattern = "right arm position"),
                    @Feature(name = "Right arm anchor",description = "The player's right arm rotation anchor (or rotation origin, i.e the point that the part will be rotated around)",type = "array",pattern = "right arm anchor"),
                    @Feature(name = "Left leg rotation",description = "The player's left leg rotation",type = "array",pattern = "left leg rotation"),
                    @Feature(name = "Left leg position",description = "The player's left leg position",type = "array",pattern = "left leg position"),
                    @Feature(name = "Left leg anchor",description = "The player's left leg rotation anchor (or rotation origin, i.e the point that the part will be rotated around)",type = "array",pattern = "left leg anchor"),
                    @Feature(name = "Right leg rotation",description = "The player's right leg rotation",type = "array",pattern = "right leg rotation"),
                    @Feature(name = "Right leg position",description = "The player's right leg position",type = "array",pattern = "right leg position"),
                    @Feature(name = "Right leg anchor",description = "The player's right leg rotation anchor (or rotation origin, i.e the point that the part will be rotated around)",type = "array",pattern = "right leg anchor"),
                    @Feature(name = "Head position",description = "The player's head position",type = "array",pattern = "head position"),
                    @Feature(name = "Head rotation",description = "The player's head rotation",type = "array",pattern = "head rotation"),
                    @Feature(name = "Head anchor",description = "The player's head rotation anchor (or rotation origin, i.e the point that the part will be rotated around)",type = "array",pattern = "head anchor"),

            }
    )
    public static class EvtOnRenderPlayer extends ScriptEvent {

        public EvtOnRenderPlayer(EntityPlayer player, ModelPlayer modelPlayer, float partialTicks, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch) {
            ModelRenderer[] modelRenderers = new ModelRenderer[]{
                    modelPlayer.bipedLeftArm,
                    modelPlayer.bipedRightArm,
                    modelPlayer.bipedLeftLeg,
                    modelPlayer.bipedRightLeg,
                    modelPlayer.bipedHead
            };

            String[] modelNames = new String[]{
                    "left arm",
                    "right arm",
                    "left leg",
                    "right leg",
                    "head"
            };
            List<ScriptTypeAccessor> accessorList = new ArrayList<>();
            accessorList.add(new ScriptTypeAccessor(new TypePlayer(player),"player"));
            accessorList.add(new ScriptTypeAccessor(new TypeNumber(limbSwing),"limb swing"));
            accessorList.add(new ScriptTypeAccessor(new TypeNumber(limbSwingAmount),"limb swing amount"));
            accessorList.add(new ScriptTypeAccessor(new TypeNumber(netHeadYaw),"head yaw"));
            accessorList.add(new ScriptTypeAccessor(new TypeNumber(headPitch),"head pitch"));
            accessorList.add(new ScriptTypeAccessor(new TypeNumber(partialTicks),"partial ticks"));
            for (int i = 0; i < modelRenderers.length; i++) {
                ModelRenderer part = modelRenderers[i];
                accessorList.add(new ScriptTypeAccessor(new TypeArray(SqriptUtils.locationToArray(part.rotateAngleX/3.14159*180,part.rotateAngleY/3.14159*180,part.rotateAngleZ/3.14159*180)),modelNames[i]+" rotation"));
                accessorList.add(new ScriptTypeAccessor(new TypeArray(SqriptUtils.locationToArray(part.offsetX,part.offsetY,part.offsetZ)),modelNames[i]+" position"));
                accessorList.add(new ScriptTypeAccessor(new TypeArray(SqriptUtils.locationToArray(part.rotationPointX,part.rotationPointY,part.rotationPointZ)),modelNames[i]+" anchor"));
            }
            setAccessors(accessorList.toArray(new ScriptTypeAccessor[0]));
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

    @Event(
            feature = @Feature(name = "Render of world",
                    description = "Called when world is rendered",
                    examples = "on render of world:",
                    pattern = "(render [of] world|world render)",
                    side = Side.CLIENT
            ),
            accessors = {
                    @Feature(name = "Partial ticks", description = "The partial ticks since last frame.", pattern = "partial ticks", type = "number"),
            }
    )
    public static class EvtOnRenderWorld extends ScriptEvent {

        public EvtOnRenderWorld(float partialTicks) {
            setAccessors(new ScriptTypeAccessor[]{new ScriptTypeAccessor(new TypeNumber(partialTicks),"partial ticks")});
        }

    }

}
