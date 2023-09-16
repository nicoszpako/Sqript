package fr.nico.sqript.mixin;

import fr.nico.sqript.forge.events.ModelPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPlayer.class)
public abstract class MixinModelPlayer extends ModelBiped {

    @Shadow
    public ModelRenderer bipedLeftArmwear;

    @Shadow
    public ModelRenderer bipedRightArmwear;

    @Shadow
    public ModelRenderer bipedLeftLegwear;

    @Shadow
    public ModelRenderer bipedRightLegwear;

    @Shadow
    public ModelRenderer bipedBodyWear;

    @Final
    @Shadow
    private ModelRenderer bipedCape;

    @Final
    @Shadow
    private ModelRenderer bipedDeadmau5Head;

    @Final
    @Shadow
    private boolean smallArms;


    /**
     * @author Nico
     */
    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo callbackInfo)
    {
        MinecraftForge.EVENT_BUS.post(new ModelPlayerEvent((EntityPlayer) entityIn, (ModelPlayer) (Object) this, Minecraft.getMinecraft().getRenderPartialTicks(), limbSwing, limbSwingAmount, netHeadYaw, headPitch, scaleFactor));
    }


}
