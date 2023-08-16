package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Button extends Widget {


    public float textScale = 1F;
    protected ResourceLocation resourceLocation;
    protected boolean hold = false;
    protected boolean hover = false;
    protected String displayText = "";
    protected int displayTextColor = 0xFFFFFFFF;

    //See constants to choose
    protected int drawType = 0;
    public static final int DRAW_TEXT = 0;
    public static final int DRAW_TEXT_AND_TEXTURE = 5;
    public static final int DRAW_ICON = 1;
    public static final int DRAW_TEXT_AND_ICON = 4;
    public static final int DRAW_ITEM = 2;

    private int buttonId;
    protected String command = "";
    protected boolean canHold = false;
    protected DisplayInfos itemDisplayInfos = new DisplayInfos(0, 0, 1, 512, 512);
    protected DisplayInfos iconDisplayInfos = new DisplayInfos(0, 0, 1, 512, 512);
    protected ItemStack itemstack;

    public Button(int width, int height, String text) {
        style.setWidth(width);
        style.setHeight(height);
        style.drawborders = false;
        displayText = text;
    }

    public Button(Style style) {
        this.style = style;
    }

    public Button(int x, int y, int width, int height, String text) {
        setX(x);
        setY(y);
        style.setWidth(width);
        style.setHeight(height);
        style.drawborders = false;
        displayText = text;
    }

    public Button(int width, int height) {
        style.setWidth(width);
        style.setHeight(height);
        style.drawborders = false;
    }



    public void checkClick(int mouseX, int mouseY) {

        if (Minecraft.getMinecraft().currentScreen == null)
            return;
        if (getFrame() != null && getFrame().isInteracting)
            return;

        if (!canHold) {
            if (!hover && isMouseOnWidget(mouseX, mouseY) && Mouse.isButtonDown(0))
                return;
            hover = isMouseOnWidget(mouseX, mouseY);
            if ((Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && isMouseOnWidget(mouseX, mouseY)) {
                if (!hold) {
                    onClick(Mouse.isButtonDown(0) ? EnumAction.MOUSE_LEFT_CLICK : EnumAction.MOUSE_RIGHT_CLICK);
                    hold = true;
                }
            } else
                hold = false;

        } else {
            if ((Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) && isMouseOnWidget(mouseX, mouseY)) {
                onClick(Mouse.isButtonDown(0) ? EnumAction.MOUSE_LEFT_CLICK : EnumAction.MOUSE_RIGHT_CLICK);
            }
        }

    }

    public boolean isCanHold() {
        return canHold;
    }

    public void setCanHold(boolean canHold) {
        this.canHold = canHold;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1);
        super.draw(mouseX, mouseY);
        if (this.getParent() != null) {
            GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(),
                    mc.displayHeight - (getHighestParent().y + getHighestParent().style.height)
                            * scaledResolution.getScaleFactor(),
                    getHighestParent().style.width * scaledResolution.getScaleFactor(),
                    getHighestParent().style.height * scaledResolution.getScaleFactor());

        } else {

            GL11.glScissor(x * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(),
                    mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor()
                            - (int) style.bordersize * scaledResolution.getScaleFactor(),
                    style.width * scaledResolution.getScaleFactor()
                            + 2 * (int) style.bordersize * scaledResolution.getScaleFactor(),
                    style.height * scaledResolution.getScaleFactor()
                            + 2 * (int) style.bordersize * scaledResolution.getScaleFactor());



        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        if (isMouseOnWidget(mouseX, mouseY)) {
            if (style.bordersize > 0)
                drawBorders(style.hoverBorderColor);
            GlStateManager.color(1, 1, 1);

            drawRect(x, y, x + style.width, y + style.height, style.hoverColor);

        } else {
            if (style.bordersize > 0)
                drawBorders(style.bordercolor);
            GlStateManager.color(1, 1, 1);
            drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);

        }

        checkClick(mouseX, mouseY);

        switch (drawType) {
            case DRAW_TEXT:
                GlStateManager.translate(this.x + this.style.width / 2f, this.y + this.style.height / 2f
                        - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * textScale) / 2, 0);
                GlStateManager.scale(textScale, textScale, 1);
                if (displayText != null)
                    drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayText, 0, 0, displayTextColor);
                break;
            case DRAW_ICON:
                if (resourceLocation != null)
                    drawIcon(resourceLocation);
                break;
            case DRAW_ITEM:
                if (itemstack != null)
                    drawItem(itemstack);
                break;
            case DRAW_TEXT_AND_ICON:
                if (displayText != null)
                    drawString(Minecraft.getMinecraft().fontRenderer, this.displayText, (int) (this.x + itemDisplayInfos.x),
                            this.y + (int) (itemDisplayInfos.y + this.style.height / 2
                                    - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2),
                            displayTextColor);
                if (resourceLocation != null)
                    drawIcon(resourceLocation);

                break;
            case DRAW_TEXT_AND_TEXTURE:
                if (displayText != null)
                    drawString(Minecraft.getMinecraft().fontRenderer, this.displayText, (int) (this.x + itemDisplayInfos.x),
                            this.y + (int) (itemDisplayInfos.y + this.style.height / 2
                                    - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2),
                            displayTextColor);
                if (resourceLocation != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableLighting();
                    //System.out.println(hold+" "+hover);
                    if(hold){
                        GlStateManager.color(0.8f, 0.8f, 0.8f);
                    }else if(hover){
                        GlStateManager.color(0.9f, 0.9f, 0.9f);
                    }else
                        GlStateManager.color(1, 1, 1);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                    drawTexturedRect(x, y, this.style.width, this.style.height);

                    GlStateManager.resetColor();
                    GlStateManager.popMatrix();
                }
            case 6:
                customRenderer(mouseX, mouseY);
                break;

        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    public void customRenderer(int mouseX, int mouseY) {

    }

    public void drawItem(ItemStack itemstack2) {
        double x = itemDisplayInfos.x;
        double y = itemDisplayInfos.y;
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + x, this.y + y, 100);
        GlStateManager.scale(itemDisplayInfos.scale, itemDisplayInfos.scale, 1);
        RenderHelper.enableGUIStandardItemLighting();

        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemstack, 0, 0);

        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,
                itemstack, 0, 0, itemstack.getCount() > 1 ? String.valueOf(itemstack.getCount()) : "");

        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public void drawIcon(ResourceLocation resourceLocation) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x, this.y, 1);
        GlStateManager.scale(iconDisplayInfos.scale, iconDisplayInfos.scale, 0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        double x = iconDisplayInfos.x;
        double y = iconDisplayInfos.y;
        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        drawTexturedRect((int) x, (int) y, (int) iconDisplayInfos.width, (int) iconDisplayInfos.height, iconDisplayInfos.u, iconDisplayInfos.v, iconDisplayInfos.u2, iconDisplayInfos.v2);
        GlStateManager.popMatrix();

    }

    public void onClick(EnumAction action) {
        ((Frame) Minecraft.getMinecraft().currentScreen).buttonClicked(action,getButtonId());
    }

    public int getHoverColor() {
        return style.hoverColor;
    }

    public void setHoverColor(int hoverColor) {
        this.style.hoverColor = hoverColor;
    }

    public int getHoverBorderColor() {
        return style.hoverBorderColor;
    }

    public void setHoverBorderColor(int hoverBorderColor) {
        this.style.hoverBorderColor = hoverBorderColor;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getDisplayTextColor() {
        return displayTextColor;
    }

    public void setDisplayTextColor(int displayTextColor) {
        this.displayTextColor = displayTextColor;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public int getDrawType() {
        return drawType;
    }

    public void setDrawType(int drawType) {
        this.drawType = drawType;
    }

    public ItemStack getItemstack() {
        return itemstack;
    }

    public void setItemstack(ItemStack itemstack) {
        this.itemstack = itemstack;
    }

    public DisplayInfos getItemDisplayInfos() {
        return itemDisplayInfos;
    }

    public void setItemDisplayInfos(DisplayInfos itemDisplayInfos) {
        this.itemDisplayInfos = itemDisplayInfos;
    }

    public DisplayInfos getIconDisplayInfos() {
        return iconDisplayInfos;
    }

    public void setIconDisplayInfos(DisplayInfos iconDisplayInfos) {
        this.iconDisplayInfos = iconDisplayInfos;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
