package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Label extends Widget {

    public boolean rightToLeft;
    protected String displayText;
    protected EnumDisplayStyle style_display = EnumDisplayStyle.NORMAL;
    protected double scale;
    protected int textColor;

    public Label(String displayText, int textColor, double scale) {
        this.displayText = displayText;
        this.textColor = textColor;
        this.scale = scale;
        this.style.width = getSizedWidth();
        this.style.height = getSizedHeight();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        GlStateManager.pushMatrix();

        if (this.getParent() != null) {
            GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(), mc.displayHeight - (getHighestParent().y + getHighestParent().style.height) * scaledResolution.getScaleFactor(), getHighestParent().style.width * scaledResolution.getScaleFactor(), getHighestParent().style.height * scaledResolution.getScaleFactor());

        } else {
            GL11.glScissor(x * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), style.width * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor(), style.height * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor());
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GlStateManager.translate(x - (rightToLeft ? fontRenderer.getStringWidth(displayText) * scale : 0), y, 0);
        GlStateManager.scale(scale, scale, 1);
        if (style_display == EnumDisplayStyle.NORMAL)
            drawString(Minecraft.getMinecraft().fontRenderer, displayText, 0, 0, textColor);
        if (style_display == EnumDisplayStyle.CENTER)
            drawCenteredString(Minecraft.getMinecraft().fontRenderer, displayText, 0, 0, textColor);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.popMatrix();
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getSizedWidth() {
        return (int) (fontRenderer.getStringWidth(getDisplayText()) * getScale());
    }

    public int getSizedHeight() {
        return (int) (fontRenderer.FONT_HEIGHT * getScale());
    }

    public EnumDisplayStyle getStyleDisplay() {
        return style_display;
    }

    public void setStyleDisplay(EnumDisplayStyle style_display) {
        this.style_display = style_display;
    }
}
