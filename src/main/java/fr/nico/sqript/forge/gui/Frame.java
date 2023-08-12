package fr.nico.sqript.forge.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;

public class Frame extends GuiScreen {

	protected List<Widget> widgets = new ArrayList<Widget>();
	List<Widget> widgetsHandlingMouseWheel = new ArrayList<Widget>();
	List<Widget> widgetsHandlingMouseClick = new ArrayList<Widget>();
	List<Widget> widgetsHandlingKeyType = new ArrayList<Widget>();
	public int interactCode;
	public boolean isInteracting = false;
	private boolean canBeClosed = true;
	public boolean drawToolTip = false;
	public List<String> tooltip = null;
	public boolean refreshing = false;
	public int tickExisted = 0;
	public Message message;
	public boolean hold = false;
	protected ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

	public ScaledResolution getScaledResolution() {
		return scaledResolution;
	}

	public void refreshScale() {
		this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
	}

	private List<String> tooltipToDraw;
	private Widget drawingToolTipWidget;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void addWidgets() {

	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		// TODO Auto-generated method stub
		super.drawHoveringText(textLines, x, y, font);
	}

	@Override
	public void initGui() {
		if (refreshing)
			this.widgets.clear();
		this.addWidgets();
		for (Widget w : widgets) {

			w.init();
		}

	}

	public void closeFrame() {
		this.mc.displayGuiScreen((GuiScreen) null);

		if (this.mc.currentScreen == null) {
			this.mc.setIngameFocus();
		}
	}

	public void showMessage(Message m) {
		m.initGui();
		m.setParentScreen(this);
		this.isInteracting = true;
		message = m;

		// Minecraft.getMinecraft().addScheduledTask(() ->
		// Minecraft.getMinecraft().displayGuiScreen(m));
	}

	public void messageResponse(Message m, EnumMessageOptions em) {
		
		this.isInteracting = false;

	}

	private void drawScrolledSplitString(String text, int startX, int startY, int width, int textColour) {
		List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);

		int count = 0;
		int lineY = startY;

		for (String line : lines) {

			if (lineY + fontRenderer.FONT_HEIGHT - startY > height) {
				break;
			}

			fontRenderer.drawStringWithShadow(line, startX, lineY, textColour);
			lineY += fontRenderer.FONT_HEIGHT;

			count++;
		}

	}

	public void onWidgetDrawn(Widget widget, int mouseX, int mouseY) {
		if (widget.getTooltip() != null) {
			if (widget.isMouseOnWidget(mouseX, mouseY)) {

				drawToolTip = true;
				tooltipToDraw = widget.getTooltip();
				drawingToolTipWidget = widget;
			} else {

				if (widget == drawingToolTipWidget) {
					drawToolTip = false;

					tooltipToDraw = null;
					drawingToolTipWidget = null;
				}
			}
		}
	}

	public void update(NBTTagCompound nbt) {
		drawToolTip = false;
		tooltipToDraw = null;
		drawingToolTipWidget = null;
	}

	public void drawToolTip(List<String> tooltip, int mouseX, int mouseY) {
		int bestWidth = 0;
		if (Minecraft.getMinecraft().currentScreen != this)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 15);
		for (String s : tooltip) {
			if (fontRenderer.getStringWidth(s) > bestWidth)
				bestWidth = fontRenderer.getStringWidth(s);
		}
		drawRect(mouseX, mouseY, mouseX + bestWidth + 10, mouseY + ((tooltip.size()) * fontRenderer.FONT_HEIGHT) + 10,
				0xBB555555);
		String s = "";

		for (String l : tooltip) {
			s += l + "\n";
		}

		drawScrolledSplitString(s, mouseX + 5, mouseY + 5, bestWidth, 0xFFFFFFFF);
		GlStateManager.popMatrix();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();
		for (Widget w : widgetsHandlingMouseWheel)
			if (i != 0) {

				w.mouseWheelInput(i);

			}
	}

	public void addWidget(Widget w) {
		isInteracting = false;
		w.setId(widgets.size());
		w.onAddToFrame(this);
		w.parentFrame = this;
		widgets.add(w);
	}

	public void removeWidget(Widget w) {
		widgets.remove(w);
	}

	public void removeWidget(int index) {
		widgets.remove(index);
	}

	public void addMouseInputListener(Widget w) {
		this.widgetsHandlingMouseWheel.add(w);
	}

	public void addMouseClickListener(Widget w) {
		this.widgetsHandlingMouseClick.add(w);
	}

	public void handleAction(Widget w, EnumAction action) {
		if (message != null) {
			message.handleAction(w, action);
		}
	}

	public void addKeyTypeListener(Widget w) {
		this.widgetsHandlingKeyType.add(w);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (message != null) {
			message.keyTyped(typedChar, keyCode);       
	        	return;
		} else {
			if (canBeClosed&&keyCode==1) {
				this.closeFrame();
			}
			for (Widget w : widgetsHandlingKeyType) {
				w.keyTyped(typedChar, keyCode);

			}
		}

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (message != null) {
			message.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			super.mouseClicked(mouseX, mouseY, mouseButton);
			for (Widget w : widgetsHandlingMouseClick) {
				w.mouseClick(mouseX, mouseY, mouseButton);

			}
		}
	}

	public void showHierarchy(List<Widget> widgets) {
		System.out.println("------ FRAME HIERARCHY ------");
		System.out.println(" | MainFrame");
		showHierarchy(widgets, 1);
	}

	public void showHierarchy(List<Widget> widgets, int tab) {

		for (Widget w : widgets) {
			/*
			if (w instanceof TabPane) {
				for (int i = 0; i < ((TabPane) w).tabs.size(); i++) {
					String stab = " ";
					for (int j = 0; j < tab; j++)
						stab += "| ";
					System.out.println(
							stab + "┗ " + " " + w.id + " " + (this.widgetsHandlingKeyType.contains(w) ? "[w] " : "")
									+ (w.toString().replaceAll("com.nicoszpako.rpworld.guiapi.", "")) + " ");
					Container s = ((TabPane) w).tabs.get(i);
					showHierarchy(s.content, tab + 1);
				}
			}

			else */
			if (w instanceof Container) {
				String stab = " ";
				for (int j = 0; j < tab; j++)
					stab += "| ";
				System.out.println(
						stab + "┗ " + " " + w.id + " " + (this.widgetsHandlingKeyType.contains(w) ? "[w] " : "")
								+ (w.toString().replaceAll("com.nicoszpako.rpworld.guiapi.", "")) + " ");
				showHierarchy(((Container) w).content, tab + 1);
			}

			else {
				String stab = " ";
				for (int j = 0; j < tab; j++)
					stab += "| ";
				System.out.println(
						stab + "┗ " + " " + w.id + " " + (this.widgetsHandlingKeyType.contains(w) ? "[w] " : "")
								+ (w.toString().replaceAll("com.nicoszpako.rpworld.guiapi.", "")) + " ");
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (message != null) {
			message.drawScreen(mouseX, mouseY, partialTicks);
			GL11.glTranslated(0, 0, -250);

			mouseX=0;
			mouseY=0;
		}
		
			if (Minecraft.getMinecraft().currentScreen == this)
				this.isInteracting = false;
			if (!Mouse.isButtonDown(0))
				hold = false;
			tickExisted++;
			for (Widget w : widgets) {
				w.draw(mouseX, mouseY);
			}
			if (drawToolTip) {
				if (tooltipToDraw != null)
					drawToolTip(tooltipToDraw, mouseX, mouseY);
			}
		
	}

	public Widget getWidgetFromId(int id) {
		for (Widget w : widgets) {
			if (w.getId() == id)
				return w;
		}
		return null;
	}

	public boolean isCanBeClosed() {
		return canBeClosed;
	}

	public void setCanBeClosed(boolean canBeClosed) {
		this.canBeClosed = canBeClosed;
	}

	public void clearWidgets() {
		widgets.clear();
	}

}
