package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Graph extends Widget {

    String[] x_data;
    double[] y_data;
    int[] vx;
    int[] vy;

    int line_width = 5;
    int line_color = 0xFF44AA44;

    public Graph(Style style, String[] x_data, double[] y_data) {
        this.setStyle(style);
        this.x_data = x_data;
        this.y_data = y_data;


        double max_value = 0;
        int height = style.height;
        int width = style.width;
        for (double i : this.y_data) {
            if (i > max_value)
                max_value = i;
        }
        int nb = this.x_data.length;
        int space = 15;
        int x_length = width - 2 * space;
        int y_length = height - 2 * space;
        vx = new int[nb];
        vy = new int[nb];

        for (int i = 0; i < this.x_data.length; i++) {
            String key = this.x_data[i];
            double value = this.y_data[i];
            vx[i] = i * (x_length / ((nb - 1 == 0 ? 1 : nb - 1)));
            vy[i] = (int) ((value / max_value) * y_length);

        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {

        if (this.getParent() != null) {
            GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(), mc.displayHeight - (getHighestParent().y + getHighestParent().style.height) * scaledResolution.getScaleFactor(), getHighestParent().style.width * scaledResolution.getScaleFactor(), getHighestParent().style.height * scaledResolution.getScaleFactor());

        } else {
            GL11.glScissor(x * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor() - (int) style.bordersize * scaledResolution.getScaleFactor(), style.width * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor(), style.height * scaledResolution.getScaleFactor() + 2 * (int) style.bordersize * scaledResolution.getScaleFactor());
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);


        int height = style.height;
        int width = style.width;
        drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);
        drawBorders(style.bordercolor);
        int space = 15;

        drawLine(x + space, y + space, x + space, y + height - space, 4);
        drawLine(x + space, y + height - space, x + width - space, y + height - space, 4);
        GL11.glColor3d(0, 0, 0);

        drawString(Minecraft.getMinecraft().fontRenderer, "€", x + space - 2, y + space - 10, 0xFFFFFFFF);

        int nb = x_data.length - 1;
        int x_length = width - 2 * space;
        int y_length = height - 2 * space;

        for (int i = 0; i <= nb; i++) {
            GL11.glColor3d(0, 0, 0);

            drawLine(x + space + ((float) i * ((float) x_length / (float) nb)), y + height - space,
                    x + space + ((float) i * ((float) x_length / (float) nb)), y + space, 1);
            drawLine(x + space, y + height - space - ((float) i * ((float) y_length / (float) nb)),
                    x + space + x_length, y + height - space - ((float) i * ((float) y_length / (float) nb)), 1);

            GlStateManager.pushMatrix();
            double s = 0.6;
            GlStateManager.translate(
                    x + space + 1 + vx[i] - Minecraft.getMinecraft().fontRenderer.getStringWidth(x_data[i]) * s / 2,
                    y + height - space / 2, 0);
            GlStateManager.scale(s, s, 1);

            drawString(Minecraft.getMinecraft().fontRenderer, x_data[i], 0, 0, 0xFFBBBBBB);
            GlStateManager.popMatrix();

            if (i > 0) {
                int x_pb = vx[i - 1];
                int y_pb = vy[i - 1];
                int x_p = vx[i];
                int y_p = vy[i];

                GlStateManager.color(0, 0, 0);
                drawLine(x + space + 1 + x_pb, y + height - y_pb + 1 - space, x + space + 1 + x_p,
                        y + height - y_p + 1 - space, 3);

            }
        }

        GlStateManager.pushMatrix();

        for (int i = 0; i <= nb; i++) {
            int x_p = vx[i];
            int y_p = vy[i];
            if (mouseX >= x + x_p + space + 1 - 2 && mouseX <= x + x_p + space + 1 + 2
                    && mouseY >= y + height - y_p - space + 1 - 2 && mouseY <= y + height - y_p - space + 1 + 2) {

                GlStateManager.translate(0, 0, 4);
                drawRect(x + x_p + space + 1 - 2, y + height - y_p - space + 1 - 2, x + x_p + space + 1 + 2,
                        y + height - y_p - space + 1 + 2, 0xFF88DD88);
                drawRect(mouseX + 4, mouseY, (int) (mouseX
                                + Minecraft.getMinecraft().fontRenderer.getStringWidth(y_data[i] + ".0 €")
                                * 0.7),
                        mouseY + 15, 0xAA333333);
                GlStateManager.pushMatrix();
                GlStateManager.translate(mouseX, mouseY, 0);

                GlStateManager.scale(0.7, 0.7, 1);
                drawString(Minecraft.getMinecraft().fontRenderer, y_data[i] + " €", 7, 3, 0xFF55AA55);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate(mouseX, mouseY, 0);

                GlStateManager.scale(0.5, 0.5, 1);

                drawString(Minecraft.getMinecraft().fontRenderer, x_data[i], 11, 19, 0xFF888888);

                GlStateManager.popMatrix();

            } else {
                GlStateManager.translate(0, 0, 4);
                drawRect(x + x_p + space + 1 - 2, y + height - y_p - space + 1 - 2, x + x_p + space + 1 + 2,
                        y + height - y_p - space + 1 + 2, 0xFF44AA44);
            }
        }

        GlStateManager.popMatrix();

    }

}
