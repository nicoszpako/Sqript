package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DropdownContainer extends Container {
    public static final int LABEL_DRAW_TEXT = 0;
    public static final int LABEL_DRAW_IMAGE = 1;
    public int label_display_mode = 0;
    public boolean displayFromRight = false;
    private String displayText = "";
    private boolean extended = false;
    private final int extendedHeight;
    private CuboidInfos buttonDimensions;
    private boolean hover;
    private boolean hold;

    public DropdownContainer(Style style, CuboidInfos buttonDimensions) {
        super(style);
        extendedHeight = style.height;
        style.height = buttonDimensions.height;
        this.setLayout(EnumLayout.BLOCK);
        this.buttonDimensions = buttonDimensions;
    }

    public static boolean isMouseInBox(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX < x2 && mouseX > x1 && mouseY < y2 && mouseY > y1;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        if (extended) this.style.height = extendedHeight;
        else this.style.height = buttonDimensions.height;
        this.extended = extended;
    }

    public CuboidInfos getButtonDimensions() {
        return buttonDimensions;
    }

    public void setButtonDimensions(CuboidInfos buttonDimensions) {
        this.buttonDimensions = buttonDimensions;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public void mouseClick(int x, int y, int mousebutton) {
        // TODO Auto-generated method stub
        super.mouseClick(x, y, mousebutton);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        if (this.getParent() instanceof ScrollPane) {
            GL11.glScissor(getParent().x * scaledResolution.getScaleFactor(),
                    mc.displayHeight - (getParent().y + getParent().style.height) * scaledResolution.getScaleFactor(),
                    getParent().style.width * scaledResolution.getScaleFactor(),
                    getParent().style.height * scaledResolution.getScaleFactor());

        } else if (this.getParent() != null && adaptScissorBoxToParent) {
            GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(),
                    mc.displayHeight - (getHighestParent().y + getHighestParent().style.height)
                            * scaledResolution.getScaleFactor(),
                    getHighestParent().style.width * scaledResolution.getScaleFactor(),
                    getHighestParent().style.height * scaledResolution.getScaleFactor());

        } else {
            GL11.glScissor((x - (int) style.bordersize) * scaledResolution.getScaleFactor(),
                    mc.displayHeight - (y + style.height + (int) style.bordersize) * scaledResolution.getScaleFactor(),
                    (2 * (int) style.bordersize + style.width) * scaledResolution.getScaleFactor(),
                    (style.height + 2 * (int) style.bordersize) * scaledResolution.getScaleFactor());

        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        {
            drawBorders(style.bordercolor);

            if (extended) {

                GlStateManager.translate(0, 0, 50);
                if (style.drawBackground) {

                    if (draw_type == DRAW_COLOR)
                        drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);
                    if (draw_type == DRAW_IMAGE) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(imageSource);
                        drawTexturedModalRect(this.x, this.y, 0, 0, (int) display.width,
                                (int) display.height);
                    }
                }
                drawTop(mouseX, mouseY);
                for (int i = 0; i < content.size(); i++) {
                    Widget w = content.get(i);
                    Widget prev = null;
                    if (i > 0)
                        prev = content.get(i - 1);

                    if (layout == EnumLayout.NONE) {
                        w.draw(mouseX, mouseY);

                    } else if (layout == EnumLayout.BLOCK) {
                        if (!displayFromRight) {
                            w.x = this.x + w.style.margin_left + this.style.padding_left;
                            w.y = this.buttonDimensions.height + this.y + (int) w.style.bordersize + w.style.margin_top + mtop + this.style.padding_top;
                            mtop += 2 * w.style.bordersize + w.style.margin_top + w.style.height + w.style.margin_bottom;
                            w.draw(mouseX, mouseY);
                        } else {
                            w.x = this.x + w.style.margin_left + this.style.padding_left - w.style.width;
                            w.y = this.buttonDimensions.height + this.y + (int) w.style.bordersize + w.style.margin_top + mtop + this.style.padding_top;
                            mtop += 2 * w.style.bordersize + w.style.margin_top + w.style.height + w.style.margin_bottom;
                            w.draw(mouseX, mouseY);
                        }
                    } else if (layout == EnumLayout.BLOCK_CENTER) {
                        w.x = this.x + ((this.style.width / 2) - (w.style.width / 2));
                        w.y = this.buttonDimensions.height + this.y + (int) w.style.bordersize + w.style.margin_top + mtop + this.style.padding_top;
                        mtop += 2 * w.style.bordersize + w.style.margin_top + w.style.height + w.style.margin_bottom;
                        w.draw(mouseX, mouseY);
                    }
                }
            } else {
                drawTop(mouseX, mouseY);
            }

        }
        mleft = 0;
        mtop = 0;
        mright = 0;
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.popMatrix();
    }

    private void drawTop(int mouseX, int mouseY) {
        checkClick(mouseX, mouseY);
        if (isMouseInBox(mouseX, mouseY, x, y, x + buttonDimensions.width, y + buttonDimensions.height)) {
            drawRect(x, y, x + buttonDimensions.width, y + buttonDimensions.height, style.hoverColor);
        } else {
            drawRect(x, y, x + buttonDimensions.width, y + buttonDimensions.height, style.backgroundcolor);
        }
        if (label_display_mode == LABEL_DRAW_TEXT) {
            drawString(Minecraft.getMinecraft().fontRenderer, displayText.replaceAll("&", "§"), x + 5, y + this.buttonDimensions.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
            drawString(Minecraft.getMinecraft().fontRenderer, extended ? "▲" : "▼", x + this.buttonDimensions.width - 10, y + this.buttonDimensions.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFFFF);
        } else if (label_display_mode == LABEL_DRAW_IMAGE) {
            GlStateManager.color(1, 1, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(imageSource);
            drawTexturedRect(this.x + display.x, this.y + display.y, display.width, display.height, display.u, display.v, display.u2, display.v2);
        } else {
            customRenderer(mouseX, mouseY);
        }

    }

    public void customRenderer(int mouseX, int mouseY) {

    }

    public void checkClick(int mouseX, int mouseY) {
        if (!hover && isMouseInBox(mouseX, mouseY, x, y, x + buttonDimensions.width, y + buttonDimensions.height) && Mouse.isButtonDown(0))
            return;
        hover = isMouseInBox(mouseX, mouseY, x, y, x + buttonDimensions.width, y + buttonDimensions.height);
        if (Mouse.isButtonDown(0)) {
            if (isMouseInBox(mouseX, mouseY, x, y, x + buttonDimensions.width, y + buttonDimensions.height)) {
                if (!hold) {
                    if (extended && getFrame().interactCode == getParent().id) {
                        extended = false;
                        getFrame().interactCode = -1;
                    } else if (getFrame().interactCode == -1) {
                        extended = true;
                        getFrame().interactCode = getParent().id;
                    }
                    hold = true;
                }
            } else if (!isMouseInBox(mouseX, mouseY, getWidgetFromId(0).x, getWidgetFromId(0).y, getWidgetFromId(0).getX2(), getWidgetFromId(0).getY2()) && getFrame().interactCode == getParent().id) {
                extended = false;
                getFrame().interactCode = -1;
            }
        } else hold = false;
    }

}
