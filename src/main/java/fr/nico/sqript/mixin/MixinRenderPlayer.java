package fr.nico.sqript.mixin;

import fr.nico.sqript.forge.SqriptForge;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.HashMap;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> {

    public MixinRenderPlayer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**
     * @author Nico
     * @reason Changing the player's skin at runtime with a script
     */
    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        ResourceLocation customSkin = SqriptForge.getClientProxy().getCustomPlayerSkin(entity.getName());

        if(customSkin == null)
            return entity.getLocationSkin();
        return customSkin;
    }
}
