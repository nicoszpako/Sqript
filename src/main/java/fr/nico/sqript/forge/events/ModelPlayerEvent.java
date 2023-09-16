package fr.nico.sqript.forge.events;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.events.EvtRender;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class ModelPlayerEvent extends PlayerEvent {
    private ModelPlayer modelPlayer;
    private float partialTicks;

    public ModelPlayerEvent(EntityPlayer player, ModelPlayer modelPlayer, float partialTicks, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, float scaleFactor) {
        super(player);
        this.modelPlayer = modelPlayer;
        this.partialTicks = partialTicks;
        try {
            ScriptContext context = ScriptManager.callEventAndGetContext(new EvtRender.EvtOnRenderPlayer(player, modelPlayer, partialTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch));
            //Check if event was called in a script
            TypeArray leftArmRotation = (TypeArray) context.getVariable("left arm rotation".hashCode());
            if (leftArmRotation == null)
                return;
            ModelRenderer[] modelRenderers = new ModelRenderer[]{
                    modelPlayer.bipedLeftArm,
                    modelPlayer.bipedRightArm,
                    modelPlayer.bipedLeftLeg,
                    modelPlayer.bipedRightLeg,
                    modelPlayer.bipedHead
            };
            TypeArray[] transformations = new TypeArray[]{
                    leftArmRotation,
                    (TypeArray) context.getVariable("left arm position".hashCode()),
                    (TypeArray) context.getVariable("left arm anchor".hashCode()),
                    (TypeArray) context.getVariable("right arm rotation".hashCode()),
                    (TypeArray) context.getVariable("right arm position".hashCode()),
                    (TypeArray) context.getVariable("right arm anchor".hashCode()),
                    (TypeArray) context.getVariable("left leg rotation".hashCode()),
                    (TypeArray) context.getVariable("left leg position".hashCode()),
                    (TypeArray) context.getVariable("left leg anchor".hashCode()),
                    (TypeArray) context.getVariable("right leg rotation".hashCode()),
                    (TypeArray) context.getVariable("right leg position".hashCode()),
                    (TypeArray) context.getVariable("right leg anchor".hashCode()),
                    (TypeArray) context.getVariable("head rotation".hashCode()),
                    (TypeArray) context.getVariable("head position".hashCode()),
                    (TypeArray) context.getVariable("head anchor".hashCode())
            };

            for (int i = 0; i < modelRenderers.length; i++) {
                TypeArray rotation = transformations[3 * i];
                TypeArray position = transformations[3 * i + 1];
                TypeArray anchor = transformations[3 * i + 2];
                ModelRenderer renderer = modelRenderers[i];
                Vec3d rotationVec = SqriptUtils.arrayToLocation(rotation.getObject());
                Vec3d positionVec = SqriptUtils.arrayToLocation(position.getObject());
                Vec3d anchorVec = SqriptUtils.arrayToLocation(anchor.getObject());

                renderer.rotateAngleX = (float) rotationVec.x / 180 * 3.14159f;
                renderer.rotateAngleY = (float) rotationVec.y / 180 * 3.14159f;
                renderer.rotateAngleZ = (float) rotationVec.z / 180 * 3.14159f;

                renderer.offsetX = (float) positionVec.x;
                renderer.offsetY = (float) positionVec.y;
                renderer.offsetZ = (float) positionVec.z;

                renderer.rotationPointX = (float) anchorVec.x;
                renderer.rotationPointY = (float) anchorVec.y;
                renderer.rotationPointZ = (float) anchorVec.z;

            }

        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public ModelPlayer getModelPlayer() {
        return modelPlayer;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}