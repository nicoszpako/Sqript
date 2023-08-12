package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class Renderer extends Widget {

    public boolean fullRenderItem = false;
    private final EnumRenderType renderType;
    private final float scale;
    private final ItemStack renderItem;

    public Renderer(int width, int height, EnumRenderType renderType, ItemStack itemStack, float scale) {
        this.renderType = renderType;
        this.renderItem = itemStack;
        this.scale = scale;
        this.style.width = width;
        this.style.height = height;
    }


    @Override
    public void draw(int mouseX, int mouseY) {
        if (style.drawBackground) {
            drawRect(this.x, this.y + style.height, this.x + style.width, this.y, style.getBackgroundcolor());
        }
        switch (renderType) {
            case ITEM:
                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.translate(this.x, this.y, -1);
                GlStateManager.scale(this.scale, this.scale, this.scale);
                Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(renderItem, 0, 0);
                if (fullRenderItem)
                    Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, renderItem, 0, 0, null);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                if (fullRenderItem && this.isMouseOnWidget(mouseX, mouseY)) {
                    GlStateManager.pushMatrix();
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    GlStateManager.translate(this.x, this.y, 1);
                    this.setTooltip(this.getItemToolTip(renderItem));
                    GlStateManager.popMatrix();
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                }
                GlStateManager.popMatrix();

                break;
            case PLAYER:
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderUtils.drawEntityOnScreen(x, (int) (y + (30 + scale)), (int) (30 + scale), (float) (x) - mouseX, (float) (y) - mouseY, this.mc.player);
                break;
        }
        super.draw(mouseX, mouseY);
    }

}

