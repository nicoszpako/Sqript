package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropSelection extends Button {

    public List<String> choices;
    public String selected;
    int selectorId;
    private String displayText;
    private boolean extended = false;
    private final int extendedHeight;
    private CuboidInfos buttonDimensions;
    private boolean hover;
    private boolean hold;

    public DropSelection(Style style, List<String> choices) {
        super(style);
        this.extendedHeight = style.height;
        selected = choices.get(0);
        selectorId = new Random().nextInt(10000);
        this.choices = choices;
        // TODO Auto-generated constructor stub
    }

    public static boolean isMouseInBox(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX < x2 && mouseX > x1 && mouseY < y2 && mouseY > y1;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
        if (extended) {
            getFrame().interactCode = selectorId;
            getFrame().isInteracting = true;
        } else {
            getFrame().isInteracting = false;
        }
    }

    public CuboidInfos getButtonDimensions() {
        return buttonDimensions;
    }

    public void setButtonDimensions(CuboidInfos buttonDimensions) {
        this.buttonDimensions = buttonDimensions;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        if (extended) GL11.glDisable(GL11.GL_SCISSOR_TEST);
        super.drawBorders(style.bordercolor);
        if (isMouseInBox(mouseX, mouseY, x, y, x + style.width, y + style.height)) {
            drawRect(x, y, x + style.width, y + style.height, style.hoverColor);
            checkClick(mouseX, mouseY);
        } else {
            drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);

        }
        if (!isMouseInBox(mouseX, mouseY, x, y, x + style.width, y + style.height * choices.size()) && Mouse.isButtonDown(0) && extended) {
            setExtended(false);
        }
        if (extended) {
            GlStateManager.translate(0, 0, 4);
            drawRect(this.x - style.bordersize, getY2() + style.bordersize, getX2() + style.bordersize, getY2(), style.bordercolor);
            GlStateManager.translate(0, 1, 0);

            int i = 1;
            for (String s : choices) {
                if (s.equals(selected)) continue;

                CuboidInfos c = new CuboidInfos(this.x, this.y + style.height * i, style.width, style.height);

                if (c.isMouseOn(mouseX, mouseY)) {

                    drawRect(c.x, c.y, c.x + c.width, c.y + c.height, 0xFF000000);
                    drawRect(c.x + 1, c.y, c.x + c.width - 1, c.y + c.height - 1, style.hoverColor);
                    if (Mouse.isButtonDown(0)) {

                        this.setExtended(false);

                        this.selected = s;
                        getFrame().handleAction(this, EnumAction.MOUSE_LEAVE);
                        getFrame().hold = true;
                        break;
                    }
                } else {
                    drawRect(c.x, c.y, c.x + c.width, c.y + c.height, 0xFF000000);
                    drawRect(c.x + 1, c.y, c.x + c.width - 1, c.y + c.height - 1, style.backgroundcolor);


                }
                drawString(Minecraft.getMinecraft().fontRenderer, s, c.x + (c.height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) / 2, c.y + c.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFBBBBBB);
                i++;
            }
            GlStateManager.translate(0, -1, 0);

        }

        drawString(Minecraft.getMinecraft().fontRenderer, selected, this.x + (this.style.height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) / 2, this.y + this.style.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
        drawString(Minecraft.getMinecraft().fontRenderer, extended ? "▲" : "▼", this.x + this.style.width - 8, this.y + this.style.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
        GL11.glColor3d(1, 1, 1);
        GlStateManager.popMatrix();
    }

    private void drawTop(int mouseX, int mouseY) {

        drawString(Minecraft.getMinecraft().fontRenderer, displayText.replaceAll("&", "§"), x + 5, y + this.buttonDimensions.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
        drawString(Minecraft.getMinecraft().fontRenderer, extended ? "▲" : "▼", x + this.buttonDimensions.width - 10, y + this.buttonDimensions.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);

    }

    @Override
    public void checkClick(int mouseX, int mouseY) {
        if (getFrame().hold == true) return;

        if (getFrame().isInteracting)
            if (getFrame().interactCode != selectorId) return;

        if (!hover && isMouseInBox(mouseX, mouseY, x, y, x + style.width, y + style.height) && Mouse.isButtonDown(0))
            return;
        hover = isMouseInBox(mouseX, mouseY, x, y, x + style.width, y + style.height);
        if (Mouse.isButtonDown(0) && isMouseInBox(mouseX, mouseY, x, y, x + style.width, y + style.height)) {
            if (!hold) {
                this.setExtended(!this.extended);
                hold = true;
            }
        } else {
            hold = false;
        }


    }

}


