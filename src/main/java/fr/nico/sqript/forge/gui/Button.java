package fr.nico.sqript.forge.gui;

import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Button extends Widget {

	protected ResourceLocation resourceLocation;
	protected boolean hold = false;
	protected boolean hover = false;
	protected String displayText = "";
	protected int displayTextColor = 0xFFFFFFFF;
	protected int drawType = 0;
	protected int actionCode = 0;
	protected String command = "";
	protected boolean canHold = false;

	public float textScale = 1F;

	protected DisplayInfos itemDisplayInfos = new DisplayInfos(0, 0, 1, 512, 512);
	protected DisplayInfos iconDisplayInfos = new DisplayInfos(0, 0, 1, 512, 512);

	public static final int DRAW_TEXT = 0;
	public static final int DRAW_ICON = 1;
	public static final int DRAW_TEXT_AND_ICON = 4;

	public static final int DRAW_ITEM = 2;

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
			if (isMouseOnWidget(mouseX, mouseY))
				hover = true;
			else
				hover = false;
			if (Mouse.isButtonDown(0) && isMouseOnWidget(mouseX, mouseY)) {
				if (!hold) {
					onClick(EnumAction.MOUSE_LEFT_CLICK);
					hold = true;
				}
			} else
				hold = false;

		} else {
			if (Mouse.isButtonDown(0) && isMouseOnWidget(mouseX, mouseY)) {
				onClick(EnumAction.MOUSE_LEFT_CLICK);
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
		super.draw(mouseX, mouseY);

		if (this.getParent() != null) {
			GL11.glScissor(getHighestParent().x * scaledResolution.getScaleFactor(),
					mc.displayHeight - (getHighestParent().y + getHighestParent().style.height)
							* scaledResolution.getScaleFactor(),
					getHighestParent().style.width * scaledResolution.getScaleFactor(),
					getHighestParent().style.height * scaledResolution.getScaleFactor());

		} else {
			GL11.glScissor(x * scaledResolution.getScaleFactor() - style.bordersize * scaledResolution.getScaleFactor(),
					mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor()
							- style.bordersize * scaledResolution.getScaleFactor(),
					style.width * scaledResolution.getScaleFactor()
							+ 2 * style.bordersize * scaledResolution.getScaleFactor(),
					style.height * scaledResolution.getScaleFactor()
							+ 2 * style.bordersize * scaledResolution.getScaleFactor());
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		if (isMouseOnWidget(mouseX, mouseY)) {
			if (style.bordersize > 0)
				drawBorders(style.hoverBorderColor);

			drawRect(x, y, x + style.width, y + style.height, style.hoverColor);

		} else {
			if (style.bordersize > 0)
				drawBorders(style.bordercolor);

			drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);

		}

		checkClick(mouseX, mouseY);

		switch (drawType) {
		case 0:
			GL11.glTranslated(this.x + this.style.width / 2, this.y + this.style.height / 2
					- (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * textScale) / 2, 0);
			GL11.glScaled(textScale, textScale, 1);
			if (displayText != null)
				drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayText, 0, 0, displayTextColor);
			break;
		case 1:
			if (resourceLocation != null)
				drawIcon(resourceLocation);
			break;
		case 2:
			if (itemstack != null)
				drawItem(itemstack);
			break;
		case 4:
			if (displayText != null)
				drawString(Minecraft.getMinecraft().fontRenderer, this.displayText, (int) (this.x + itemDisplayInfos.x),
						this.y + (int) (itemDisplayInfos.y + this.style.height / 2
								- Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2),
						displayTextColor);
			if (resourceLocation != null)
				drawIcon(resourceLocation);

			break;
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}

	public void drawItem(ItemStack itemstack2) {
		double x = itemDisplayInfos.x;
		double y = itemDisplayInfos.y;
		GL11.glPushMatrix();
		GL11.glTranslated(this.x + x, this.y + y, 101);
		GL11.glScaled(itemDisplayInfos.scale, itemDisplayInfos.scale, 1);
		RenderHelper.enableGUIStandardItemLighting();

		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemstack, 0, 0);

		Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,
				itemstack, 0, 0, itemstack.getCount() > 1 ? String.valueOf(itemstack.getCount()) : "");

		RenderHelper.disableStandardItemLighting();

		GL11.glPopMatrix();
	}

	public void drawIcon(ResourceLocation resourceLocation) {
		GL11.glPushMatrix();

		GL11.glTranslated(this.x, this.y, 1);
		GL11.glScaled(iconDisplayInfos.scale, iconDisplayInfos.scale, 0F);
		GL11.glColor4f(3F, 3F, 3F, 10F);

		// GL11.glScaled(iconDisplayInfos.scale, iconDisplayInfos.scale, 1);
		double x = iconDisplayInfos.x;
		double y = iconDisplayInfos.y;

		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
		this.drawTexturedRect((int) x, (int) y, (int) iconDisplayInfos.width, (int) iconDisplayInfos.height);
		GL11.glPopMatrix();

	}

	public static void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRect(x, y, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
	}

	public static void drawTexturedRect(double x, double y, double w, double h, double u1, double v1, double u2,
			double v2) {
		try {
			GlStateManager.pushMatrix();

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
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void onClick(EnumAction action) {
		if (this.getFrame().message != null) {
			this.getFrame().message.handleAction(this, action);
			return;
		}
		((Frame) Minecraft.getMinecraft().currentScreen).handleAction(this, action);
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

	public int getActionCode() {
		return actionCode;
	}

	public void setActionCode(int actionCode) {
		this.actionCode = actionCode;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
