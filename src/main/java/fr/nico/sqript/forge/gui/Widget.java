package fr.nico.sqript.forge.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class Widget extends Gui {

	protected int id;
	protected Minecraft mc = Minecraft.getMinecraft();
	protected RenderItem itemRender = mc.getRenderItem();
	protected FontRenderer fontRenderer = mc.fontRenderer;
	protected ScaledResolution scaledResolution = new ScaledResolution(mc);
	protected int x=0, y=0;
	public Style style = new Style();
	public boolean isRelativeFixed = false;
	public int fixed_x=0,fixed_y=0;
	private Container parent = null;
	private List<String> tooltip = null;
	public boolean highestParent = false;
	//reference a la frame parente
	public Frame parentFrame;
	public Style getStyle() {
		return style;
	}

	public void fix(int x, int y) {
		this.isRelativeFixed=true;
		this.fixed_x=x;
		this.fixed_y=y;
	}
	
	public void refreshScale(){
		this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
	}
	
	public List<String> getItemToolTip(ItemStack p_191927_1_)
    {
        List<String> list = p_191927_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, p_191927_1_.getItem().getForgeRarity(p_191927_1_).getColor() + (String)list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + (String)list.get(i));
            }
        }

        return list;
    }
	
	public void setStyle(Style style) {
		this.style = style;
	}

	public void draw(int mouseX, int mouseY) {

		try{
			((Frame)Minecraft.getMinecraft().currentScreen).onWidgetDrawn( this, mouseX,mouseY);

		}catch(Exception e){
			
		}


	}

	public CuboidInfos getCuboidInfos(){
		return new CuboidInfos(this.x,this.y,this.style.getWidth(),this.style.getHeight());
	}
	
	public Widget getHighestParent() {
		if (getParent() == null)
			return null;
		Widget c = null;
		if (getParent() != null)
			c = getParent();
		if (c.getParent() != null && !c.highestParent)
			return c.getHighestParent();
		else
			return c;
	}

	public void drawItemStack(ItemStack stack, int x, int y, String altText) {
		GL11.glPushMatrix();
		
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GL11.glPopMatrix();
	}
	
	public void drawItem(ItemStack itemstack,double x, double y) {
	
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 101);
		RenderHelper.enableGUIStandardItemLighting();
	
	
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemstack,0,0);

		Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,itemstack, 0,0,itemstack.getCount()>1?String.valueOf(itemstack.getCount()):"");


		RenderHelper.disableStandardItemLighting();

	
		GL11.glPopMatrix();
	}
	public static int computeGuiScale() {
		Minecraft mc = Minecraft.getMinecraft();
		int scaleFactor = 1;

		int k = mc.gameSettings.guiScale;

		if (k == 0) {
			k = 1000;
		}

		while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
				&& mc.displayHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}
		return scaleFactor;
	}

	public void drawBorders(int color) {
		if (style.bordersize > 0 && style.drawborders) {
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 0);
			drawRect(this.x - style.bordersize, this.y - style.bordersize, this.x, getY2(), color);
			drawRect(this.x - style.bordersize, getY2() + style.bordersize, getX2() + style.bordersize, getY2(), color);
			drawRect(this.x - style.bordersize, getY(), getX2() + style.bordersize, getY() - style.bordersize, color);
			drawRect(getX2(), getY2() + style.bordersize, getX2() + style.bordersize, this.y - style.bordersize, color);
			GL11.glPopMatrix();
		}
	}

	public void init() {

	}


	public static void drawTexturedRect(double x, double y, double w, double h, double u1, double v1, double u2, double v2)
	{
		try
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + w, y, 0).tex(u2, v1).endVertex();
			vertexbuffer.pos(x, y, 0).tex(u1, v1).endVertex();
			vertexbuffer.pos(x, y + h, 0).tex(u1, v2).endVertex();
			vertexbuffer.pos(x + w, y + h, 0).tex(u2, v2).endVertex();
			// renderer.finishDrawing();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isMouseOnWidget(int mouseX, int mouseY) {
		if(Minecraft.getMinecraft().currentScreen instanceof Frame)
			if((Frame)Minecraft.getMinecraft().currentScreen!=this.getFrame())return false;
		boolean isMouseOnWidget = mouseX >= this.x && mouseX <= this.x + this.style.width && mouseY >= this.y
				&& mouseY <= this.y + this.style.getHeight();
		boolean isMouseOnParent = true;
		if (parent != null)
			isMouseOnParent = mouseX >= parent.x && mouseX <= parent.x + parent.style.width && mouseY >= parent.y
					&& mouseY <= parent.y + parent.style.height;

		return isMouseOnWidget && isMouseOnParent;
	}

	public static int xFromCenter(int a) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		return sr.getScaledWidth() / 2 + a;
	}

	public static int yFromCenter(int a) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		return sr.getScaledHeight() / 2 + a;
	}

	public int getX2() {
		return this.x + this.style.width;
	}

	public int getY2() {
		return this.y + this.style.height;
	}





	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}



	public Container getParent() {
		return parent;
	}

	public void setParent(Container parent) {
		this.parent = parent;
	}

	public void mouseWheelInput(int i) {

	}

	public void mouseClick(int x, int y, int mousebutton) {

	}

	public void onAddToFrame(Frame frame) {
		this.parentFrame=frame;
	}

	public void onAddToContainer(Container container) {
		this.parentFrame=container.parentFrame;
	}

	

	public void keyTyped(char typedChar, int keyCode) {

	}

	public static void drawLine(double x,double y,double x2, double y2, float lineWidth) {
		GlStateManager.enableBlend();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.disableTexture2D();
		GlStateManager.glLineWidth(lineWidth);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(x, y,0).endVertex();
		vertexbuffer.pos(x2, y2, 0).endVertex();

		// renderer.finishDrawing();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);

	}
	
	public List<String> getTooltip() {
		return tooltip;
	}

	public void setTooltip(List<String> tooltip) {
		this.tooltip = tooltip;
	}
	
	public Frame getFrame(){
		if(Minecraft.getMinecraft().currentScreen instanceof Frame)
		return (Frame)Minecraft.getMinecraft().currentScreen;
		else return null;
	}

}
