package fr.nico.sqript.forge.client;

import fr.nico.sqript.forge.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ClientProxy extends CommonProxy {

    private final HashMap<String, BufferedImage> customPlayerBufferedSkins = new HashMap<>();
    private final HashMap<String, ResourceLocation> customPlayerSkins = new HashMap<>();

    public ResourceLocation getCustomPlayerSkin(String name){
        ResourceLocation rl = getCustomPlayerSkins().get(name);
        if(rl == null) {
            BufferedImage image = customPlayerBufferedSkins.get(name);
            if (image == null) {
                return null;
            }
            rl = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("custom_skin_" + name, new DynamicTexture(image));
            getCustomPlayerSkins().put(name, rl);
        }
        return rl;
    }

    public HashMap<String, ResourceLocation> getCustomPlayerSkins() {
        return customPlayerSkins;
    }

    public HashMap<String, BufferedImage> getCustomPlayerBufferedSkins() {
        return customPlayerBufferedSkins;
    }

}
