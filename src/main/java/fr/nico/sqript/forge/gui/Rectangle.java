package fr.nico.sqript.forge.gui;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Rectangle extends Widget {


    public Rectangle(int width, int height, int color) {
        this.style.width = width;
        this.style.height = height;
        this.style.backgroundcolor = color;

    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();


        super.draw(mouseX, mouseY);
        drawBorders(style.bordercolor);
        drawRect(this.x, this.y, getX2(), getY2(), style.backgroundcolor);


        GlStateManager.popMatrix();
    }

}
