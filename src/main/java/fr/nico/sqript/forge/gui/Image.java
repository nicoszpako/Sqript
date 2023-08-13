package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Image extends Widget {

    public String imageName;
    public ResourceLocation image;
    public boolean modal = false;
    public boolean useUvs = false;
    public double[] Uvs = new double[3];

    public Image(ResourceLocation resourceLocation, int width, int height) {
        this.style.width = width;
        this.style.height = height;
        this.imageName = resourceLocation.toString();
        image = resourceLocation;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();

        drawBorders(this.style.getBordercolor());
        super.draw(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        if (modal) {
            this.drawTexturedModalRect(x, y, (int) Uvs[0], (int) Uvs[1], this.style.width, this.style.height);
        } else if (useUvs) {
            drawTexturedRect(x, y, this.style.width, this.style.height, Uvs[0], Uvs[1], Uvs[2], Uvs[3]);
        } else {
            drawTexturedRect(x, y, this.style.width, this.style.height);

        }
        GlStateManager.resetColor();

        GlStateManager.popMatrix();

    }


}
