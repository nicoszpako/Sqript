package fr.nico.sqript.forge.gui;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class LabelArea extends Label {

    public LabelArea(String displayText, int textColor, double scale, int width) {
        super(displayText, textColor, scale);
        this.style.width = width;
        List<String> lines = fontRenderer.listFormattedStringToWidth(displayText, (int) (this.style.width * (1 / scale)));
        System.out.println(lines.size());
        this.style.height = (int) (lines.size() * fontRenderer.FONT_HEIGHT * scale);
    }


    @Override
    public void draw(int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 2);
        GlStateManager.scale(scale, scale, scale);

        drawScrolledSplitString(text, 0, 0, (int) (this.style.width * (1 / scale)), this.textColor);
        GlStateManager.popMatrix();
    }

    private void drawScrolledSplitString(String text, int startX, int startY, int width, int textColour) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);

        int lineY = startY;

        for (String line : lines) {

            fontRenderer.drawString(line, startX, lineY, textColour, true);
            lineY += fontRenderer.FONT_HEIGHT;


        }
    }
}
