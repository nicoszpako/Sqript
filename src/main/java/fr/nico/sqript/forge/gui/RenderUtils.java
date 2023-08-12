package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderUtils {
    public static double zDepth = 0.0D;
    public static final double circleSteps = 30.0;
    public static int color;

    public static void drawItem(double x, double y, double scale, ItemStack itemstack) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 100);
        GlStateManager.scale(scale, scale, 1);
        RenderHelper.enableGUIStandardItemLighting();

        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemstack, 0, 0);

        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,
                itemstack, 0, 0, itemstack.getCount() > 1 ? String.valueOf(itemstack.getCount()) : "");

        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public static void drawTitleContainer(int left, int top, int width, int height, String text) {

        GlStateManager.pushMatrix();
        Gui.drawRect(left, top, left+width, top+10, 0xcc000000);
        Gui.drawRect(left, top+10, left+width, top+height, 0x88000000);
        GlStateManager.translate(left+2, top+2, 0);
        GlStateManager.scale(0.7, 0.7, 1);
        Minecraft.getMinecraft().fontRenderer.drawString(text, 0, 0, 0xFFFFFFFF);
        GL11.glColor3f(1,1,1);
        GlStateManager.popMatrix();
    }

    public static int adjustPixelBrightness(int colour, int brightness) {
        int r = colour >> 16 & 0xff;
        int g = colour >> 8 & 0xff;
        int b = colour >> 0 & 0xff;
        r = Math.min(Math.max(0, r + brightness), 0xff);
        g = Math.min(Math.max(0, g + brightness), 0xff);
        b = Math.min(Math.max(0, b + brightness), 0xff);
        return colour & 0xff000000 | r << 16 | g << 8 | b;
    }

    public static void drawLine(double x, double y, double x2, double y2, float lineWidth) {
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth);
        GlStateManager.color(0, 255, 255, 255);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x, y, RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x2, y2, RenderUtils.zDepth).endVertex();

        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

    }

    public static void disableStencil() {
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.disableDepth();

        // set the zDepth to 0 to make sure there arent any problems drawing
        // other things when circular map isnt drawn
        RenderUtils.zDepth = 0.0;
    }

    public static void drawArrow(double x, double y, double angle, double length) {
        // angle the back corners will be drawn at relative to the pointing
        // angle
        double arrowBackAngle = 0.75D * Math.PI;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x + length * Math.cos(angle), y + length * Math.sin(angle), RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x + length * 0.5D *
                Math.cos(angle -
                        arrowBackAngle), y + length * 0.5D *
                Math.sin(angle -
                        arrowBackAngle), RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x + length * 0.3D *
                Math.cos(angle + Math.PI), y +
                length * 0.3D *
                        Math.sin(angle + Math.PI), RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x + length * 0.5D *
                Math.cos(angle +
                        arrowBackAngle), y + length * 0.5D *
                Math.sin(angle +
                        arrowBackAngle), RenderUtils.zDepth).endVertex();
        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCentredString(int x, int y, int colour, String formatString, Object... args) {
        Minecraft mc = Minecraft.getMinecraft();
        // mc.renderEngine.resetBoundTexture();
        FontRenderer fr = mc.fontRenderer;
        String s = String.format(formatString, args);
        int w = fr.getStringWidth(s);
        fr.drawStringWithShadow(s, x - w / 2, y, colour);
    }

    public static void drawCircle(double x, double y, double r) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x, y, RenderUtils.zDepth).endVertex();
        // for some the circle is only drawn if theta is decreasing rather than
        // ascending
        double end = Math.PI * 2.0;
        double incr = end / RenderUtils.circleSteps;
        for (double theta = -incr; theta < end; theta += incr) {
            vertexbuffer.pos(x + r * Math.cos(-theta), y + r * Math.sin(-theta), RenderUtils.zDepth).endVertex();
        }
        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircleBorder(double x, double y, double r, double width) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION);
        // for some the circle is only drawn if theta is decreasing rather than
        // ascending
        double end = Math.PI * 2.0;
        double incr = end / RenderUtils.circleSteps;
        double r2 = r + width;
        for (double theta = -incr; theta < end; theta += incr) {
            vertexbuffer.pos(x + r * Math.cos(-theta), y + r * Math.sin(-theta), RenderUtils.zDepth).endVertex();
            vertexbuffer.pos(x + r2 * Math.cos(-theta), y + r2 * Math.sin(-theta), RenderUtils.zDepth).endVertex();
        }
        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRoundRect(double x, double y, double w, double h, double r) {
        drawRect(x, y + r, w, h - 2 * r);
        drawRect(x + r, y, w - 2 * r, h);
        drawCircle(x + r, y + r, r);
        drawCircle(x + w - r, y + r, r);
        drawCircle(x + r, y + h - r, r);
        drawCircle(x + w - r, y + h - r, r);

    }

    public static void drawRoundRectWithBorder(double x, double y, double w, double h, double r, long bordercolor, long intcolor, float borderSize) {
        setColour((int) bordercolor);
        drawRoundRect(x, y, w, h, r);
        setColour((int) intcolor);
        drawRoundRect(x + borderSize, y + borderSize, w - 2 * borderSize, h - 2 * borderSize, r);

    }

    public static void drawRect(double x, double y, double w, double h) {
        drawRect(x, y, w, h, zDepth);
    }


    public static void drawRect(double x, double y, double w, double h, double z) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x + w, y, z).endVertex();
        vertexbuffer.pos(x, y, z).endVertex();
        vertexbuffer.pos(x, y + h, z).endVertex();
        vertexbuffer.pos(x + w, y + h, z).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRectBorder(double x, double y, double w, double h, double bw) {
        // top border
        RenderUtils.drawRect(x - bw, y - bw, w + bw + bw, bw);
        // bottom border
        RenderUtils.drawRect(x - bw, y + h, w + bw + bw, bw);
        // left border
        RenderUtils.drawRect(x - bw, y, bw, h);
        // right border
        RenderUtils.drawRect(x + w, y, bw, h);
    }

    public static void drawString(int x, int y, int colour, String formatString, Object... args) {
        Minecraft mc = Minecraft.getMinecraft();
        // mc.renderEngine.resetBoundTexture();
        FontRenderer fr = mc.fontRenderer;
        String s = String.format(formatString, args);
        fr.drawStringWithShadow(s, x, y, colour);
    }

    // draw rectangle with texture stretched to fill the shape
    public static void drawTexturedRect(double x, double y, double w, double h) {
        drawTexturedRect(x, y, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
    }

    // draw rectangle with texture UV coordinates specified (so only part of the
    // texture fills the rectangle).
    public static void drawTexturedRect(double x, double y, double w, double h, double u1, double v1, double u2, double v2) {
        try {
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x + w, y, RenderUtils.zDepth).tex(u2, v1).endVertex();
            vertexbuffer.pos(x, y, RenderUtils.zDepth).tex(u1, v1).endVertex();
            vertexbuffer.pos(x, y + h, RenderUtils.zDepth).tex(u1, v2).endVertex();
            vertexbuffer.pos(x + w, y + h, RenderUtils.zDepth).tex(u2, v2).endVertex();
            // renderer.finishDrawing();
            tessellator.draw();
            GlStateManager.disableBlend();
        } catch (NullPointerException e) {
            //OrycialMod.log.error("MwRender.drawTexturedRect: null pointer exception");
        }
    }

    public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x1, y1, RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x2, y2, RenderUtils.zDepth).endVertex();
        vertexbuffer.pos(x3, y3, RenderUtils.zDepth).endVertex();
        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /*
     * Drawing Methods
     *
     * Note that EntityRenderer.setupOverlayRendering must be called before
     * drawing for the scene to appear correctly on the overlay. If these
     * functions are called from the hookUpdateCameraAndRender method of Mw this
     * will have already been done.
     */

    public static int getAverageColourOfArray(int[] pixels) {
        int count = 0;
        double totalA = 0.0;
        double totalR = 0.0;
        double totalG = 0.0;
        double totalB = 0.0;
        for (int pixel : pixels) {
            double a = pixel >> 24 & 0xff;
            double r = pixel >> 16 & 0xff;
            double g = pixel >> 8 & 0xff;
            double b = pixel >> 0 & 0xff;

            totalA += a;
            totalR += r * a / 255.0;
            totalG += g * a / 255.0;
            totalB += b * a / 255.0;

            count++;
        }

        totalR = totalR * 255.0 / totalA;
        totalG = totalG * 255.0 / totalA;
        totalB = totalB * 255.0 / totalA;
        totalA = totalA / count;

        return ((int) totalA & 0xff) << 24 |
                ((int) totalR & 0xff) << 16 |
                ((int) totalG & 0xff) << 8 |
                (int) totalB & 0xff;
    }

    public static int getAverageOfPixelQuad(int[] pixels, int offset, int scanSize) {
        int p00 = pixels[offset];
        int p01 = pixels[offset + 1];
        int p10 = pixels[offset + scanSize];
        int p11 = pixels[offset + scanSize + 1];

        // ignore alpha channel
        int r = (p00 >> 16 & 0xff) + (p01 >> 16 & 0xff) + (p10 >> 16 & 0xff) + (p11 >> 16 & 0xff);
        r >>= 2;
        int g = (p00 >> 8 & 0xff) + (p01 >> 8 & 0xff) + (p10 >> 8 & 0xff) + (p11 >> 8 & 0xff);
        g >>= 2;
        int b = (p00 & 0xff) + (p01 & 0xff) + (p10 & 0xff) + (p11 & 0xff);
        b >>= 2;
        return 0xff000000 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff;
    }

    public static int getBoundTextureId() {
        return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    public static int getTextureHeight() {
        return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    }

    public static int getTextureWidth() {
        return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
    }

    public static int multiplyColours(int c1, int c2) {
        float c1A = c1 >> 24 & 0xff;
        float c1R = c1 >> 16 & 0xff;
        float c1G = c1 >> 8 & 0xff;
        float c1B = c1 >> 0 & 0xff;
        float c2A = c2 >> 24 & 0xff;
        float c2R = c2 >> 16 & 0xff;
        float c2G = c2 >> 8 & 0xff;
        float c2B = c2 >> 0 & 0xff;
        int r = (int) (c1R * c2R / 255.0f) & 0xff;
        int g = (int) (c1G * c2G / 255.0f) & 0xff;
        int b = (int) (c1B * c2B / 255.0f) & 0xff;
        int a = (int) (c1A * c2A / 255.0f) & 0xff;
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static void printBoundTextureInfo(int texture) {
        int w = getTextureWidth();
        int h = getTextureHeight();
        int depth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL12.GL_TEXTURE_DEPTH);
        int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        //Orycial.log.error("texture %d parameters: width=%d, height=%d, depth=%d, format=%08x", texture, w, h, depth, format);
    }

    public static void resetColour() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void setCircularStencil(double x, double y, double r) {
        GlStateManager.enableDepth();
        // disable drawing to the color buffer.
        // circle will only be drawn to depth buffer.
        GlStateManager.colorMask(false, false, false, false);
        // enable writing to depth buffer
        GlStateManager.depthMask(true);

        // Clearing the depth buffer causes problems with shader mods.
        // I guess we just have to hope that the rest of the depth buffer
        // contains z values greater than 2000 at this stage in the frame
        // render.
        // It would be much easier to use the stencil buffer instead, but it is
        // not specifically requested in the Minecraft LWJGL display setup code.
        // So the stencil buffer is only available on GL implementations that
        // set it up by default.

        // clear depth buffer to z = 3000.0
        // GlStateManager.clearDepth(3000.0);
        // GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

        // always write to depth buffer
        GlStateManager.depthFunc(GL11.GL_ALWAYS);

        // draw stencil pattern (filled circle at z = 0000.0)
        // map will be drawn behind the stencil
        RenderUtils.setColour(0xffffffff);
        RenderUtils.zDepth = 0.0;
        RenderUtils.drawCircle(x, y, r);
        RenderUtils.zDepth = -1.0;

        // re-enable drawing to colour buffer
        GlStateManager.colorMask(true, true, true, true);
        // disable drawing to depth buffer
        GlStateManager.depthMask(false);
        // only draw pixels with z values that are greater
        // than the value in the depth buffer.
        // The overlay is drawn at 2000 so this will pass inside
        // the circle (2000 > 1000) but not outside (2000 <= 3000).
        GlStateManager.depthFunc(GL11.GL_GREATER);
    }

    public static void setColour(int colour) {
        color = colour;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color((colour >> 16 & 0xff) / 255.0f, (colour >> 8 & 0xff) /
                255.0f, (colour & 0xff) /
                255.0f, (colour >> 24 & 0xff) / 255.0f);
        GlStateManager.disableBlend();
    }

    public static void setColourWithAlphaPercent(int colour, int alphaPercent) {
        setColour((alphaPercent * 0xff / 100 & 0xff) << 24 | colour & 0xffffff);
    }


    public static void scissorBox(GuiScreen currentScreen, float x, float g, float width, float height) {
        int realWidth = (int) (width - x);
        int realHeight = (int) (height - g);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int factor = sr.getScaleFactor();
        int bottomY = (int) (currentScreen.height - height);
        GL11.glScissor((int) (x * factor), bottomY * factor, realWidth * factor, realHeight * factor);
    }

    public static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f;

        f = yawOffset - prevYawOffset;
        while (f < -180.0F) {
            f += 360.0F;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 100.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void drawPlayerOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 10F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(25.0F, 0.0F, 1.0F, 0.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        RenderHelper.enableStandardItemLighting();
        ent.renderYawOffset = 0;
        ent.rotationYawHead = 0;
        ent.rotationPitch = 0;
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
    }


    // A better implementation of a circular stencil using the stencil buffer
    // rather than the depth buffer can be found below. It works only on GL
    // implementations that attach a stencil buffer by default (e.g. Intel, but
    // not on Nvidia).
    //
    // To fix this we would need to change the display create line in
    // 'Minecraft.java' file from:
    // Display.create((new PixelFormat()).withDepthBits(24));
    // to:
    // Display.create((new PixelFormat()).withDepthBits(24).withStencilBits(8));
    //
    // Then we could use the stencil buffer and the the circular map would have
    // far less problems.
    //
    // I suppose it would also be possible to detect the number of stencil bits
    // available at runtime using GL11.glGetInteger(GL11.GL_STENCIL_BITS) and
    // only use the depth buffer stencil algorithm if it returns 0. But this
    // doesn't solve the problem of the stencil buffer not being initialized by
    // default on some systems.

    /*
     * public static void setCircularStencil(double x, double y, double r) {
     * GL11.glEnable(GL11.GL_STENCIL_TEST); // disable drawing to the color and
     * depth buffers. // circle will only be drawn to stencil buffer.
     * GL11.glColorMask(false, false, false, false); GL11.glDepthMask(false); //
     * set up stencil func and op so that a 1 is always written to the stencil
     * buffer // whenever a pixel is drawn. GL11.glStencilFunc(GL11.GL_NEVER, 1,
     * 0x01); // replace stencil buffer value with 1 whenever stencil test
     * fails. // keep stencil buffer value otherwise.
     * GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP); // enable
     * writing to 8 bits of the stencil buffer GL11.glStencilMask(0x01); //
     * clear stencil buffer, with mask 0xff
     * GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // draw stencil pattern
     * Render.setColour(0xffffffff); Render.drawCircle(x, y, r);
     *
     * // re-enable drawing to colour and depth buffers GL11.glColorMask(true,
     * true, true, true); // probably shouldn't enable? ->
     * GL11.glDepthMask(true); // disable writing to stencil buffer
     * GL11.glStencilMask(0x00); // draw only when stencil buffer value == 1
     * (inside circle) GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0x01); }
     *
     * public static void disableStencil() {
     * GL11.glDisable(GL11.GL_STENCIL_TEST); }
     */
}
