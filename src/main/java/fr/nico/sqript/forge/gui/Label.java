package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class Label extends Widget {

    public boolean rightToLeft;
    protected String[] displayLines;
    protected String text;
    protected EnumDisplayStyle style_display = EnumDisplayStyle.NORMAL;
    protected double scale;
    protected int textColor;

    public Label(String displayText, int textColor, double scale) {
        text = displayText;
        this.displayLines = displayText.replaceAll("&", "\247").split("\\\\n");
        //System.out.println("Actual string : " + Arrays.toString(this.displayLines));
        this.textColor = textColor;
        this.scale = scale;
        this.style.width = getSizedWidth();
        this.style.height = getSizedHeight();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);

        if (this.getParent() != null) {
            GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(), mc.displayHeight - (getHighestParent().y + getHighestParent().style.height) * scaledResolution.getScaleFactor(), getHighestParent().style.width * scaledResolution.getScaleFactor(), getHighestParent().style.height * scaledResolution.getScaleFactor());
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

        } else {
            //GL11.glScissor(x * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), style.width * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor(), style.height * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor());
        }
        int i = 0;
        for (String displayText : displayLines) {
            GlStateManager.pushMatrix();
            //System.out.println(x - (rightToLeft ? fontRenderer.getStringWidth(displayText) * scale : 0));
            GlStateManager.translate(x - (rightToLeft ? fontRenderer.getStringWidth(displayText) * scale : 0), y+i*Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT*scale, 0);
            GlStateManager.scale(scale, scale, 1);
            if (style_display == EnumDisplayStyle.NORMAL)
                drawString(Minecraft.getMinecraft().fontRenderer, displayText, 0, 0, textColor);
            if (style_display == EnumDisplayStyle.CENTER)
                drawCenteredString(Minecraft.getMinecraft().fontRenderer, displayText, (int) (style.getWidth()/2/getScale()), 0, textColor);
            GlStateManager.popMatrix();
            i++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    public String getDisplayText() {
        return text;
    }

    public void setDisplayText(String displayText) {
        this.text = displayText;
        this.displayLines = displayText.replaceAll("&", "\247").split("\\\\n");
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
        return Arrays.stream(displayLines).map(s->fontRenderer.getStringWidth(s)*getScale()).max(Double::compare).get().intValue();
    }

    public int getSizedHeight() {
        return (int) (fontRenderer.FONT_HEIGHT * getScale() * displayLines.length);
    }

    public EnumDisplayStyle getStyleDisplay() {
        return style_display;
    }

    public void setStyleDisplay(EnumDisplayStyle style_display) {
        this.style_display = style_display;
    }
}
