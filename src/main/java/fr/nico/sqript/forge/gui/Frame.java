package fr.nico.sqript.forge.gui;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.events.EvtGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Frame extends GuiScreen {

    protected Vector2f origin = new Vector2f(0, 0);
    public int interactCode;
    public boolean isInteracting = false;
    private boolean drawToolTip = true;
    public List<String> tooltip = null;
    public boolean refreshing = true;
    public int tickExisted = 0;
    public Message message;
    public boolean hold = false;
    public boolean specialInteractionMode = false;
    protected List<Widget> blankWidgets = new ArrayList<Widget>();
    protected List<Widget> runningWidgets = new ArrayList<Widget>();
    protected ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    List<IMouseWheelListener> widgetsHandlingMouseWheel = new ArrayList<IMouseWheelListener>();
    List<IClickListener> widgetsHandlingMouseClick = new ArrayList<IClickListener>();
    List<IKeyListener> widgetsHandlingKeyType = new ArrayList<IKeyListener>();
    private boolean canBeClosed = true;
    private List<String> tooltipToDraw;
    private Widget drawingToolTipWidget;

    public static final IAnchor top_left = (x,y,w,h) -> new Vector2f(x,y);
    public static final IAnchor center = (x,y,w,h) -> new Vector2f(Widget.xFromCenter(x)-w/2,Widget.yFromCenter(y)-h/2);

    private IAnchor anchor;

    public Frame(){
        this(center);
    }

    public Frame(IAnchor anchor){
        this.anchor = anchor;
    }

    public static void drawUncoloredRect(int left, int top, int right, int bottom) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    public void refreshScale() {
        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void addWidgets() {
        for (Widget blankWidget : blankWidgets) {
            addRunningWidget(blankWidget);
        }
    }

    public boolean isDrawToolTip() {
        return drawToolTip;
    }

    public void setDrawToolTip(boolean drawToolTip) {
        this.drawToolTip = drawToolTip;
    }

    @Override
    public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
        // TODO Auto-generated method stub
        super.drawHoveringText(textLines, x, y, font);
    }

    @Override
    public void initGui() {
        if (refreshing)
            this.runningWidgets.clear();
        this.addWidgets();
        for (Widget w : runningWidgets) {
            w.init();
        }

    }

    public void closeFrame() {
        this.mc.displayGuiScreen(null);

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
        GlStateManager.translate(0, 0, 150);
        for (String s : tooltip) {
            if (fontRenderer.getStringWidth(s) > bestWidth)
                bestWidth = fontRenderer.getStringWidth(s);
        }
        GlStateManager.translate(mouseX, mouseY,0);
        GlStateManager.scale(0.8,0.8,1);
        drawRect(0,0, bestWidth + 7, ((tooltip.size()) * fontRenderer.FONT_HEIGHT) + 5,
                0x55000000);
        String s = "";

        for (String l : tooltip) {
            s += l + "\n";
        }

        drawScrolledSplitString(s, 3, 3, bestWidth, 0xFFFFFFFF);
        GlStateManager.popMatrix();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        for (IMouseWheelListener w : widgetsHandlingMouseWheel)
            if (i != 0) {

                w.mouseWheelInput(i);

            }
    }

    public void addWidget(Widget w){
        blankWidgets.add(w);
    }

    public void addRunningWidget(Widget w) {
        //System.out.println("Adding widget :"+w);

        isInteracting = false;
        Vector2f position = anchor.transformPosition(w.initX,w.initY,w.style.width,w.style.height);
        //System.out.println("position : "+position);
        w.x = (int) position.x+ (int)origin.x;
        w.y = (int) position.y+ (int)origin.y;
        w.setId(runningWidgets.size());
        w.onAddToFrame(this);
        w.parentFrame = this;
        runningWidgets.add(w);
    }

    public void removeWidget(Widget w) {
        blankWidgets.remove(w);
    }

    public void removeWidget(int index) {
        blankWidgets.remove(index);
    }

    public void addMouseInputListener(IMouseWheelListener w) {
        this.widgetsHandlingMouseWheel.add(w);
    }

    public void addMouseClickListener(IClickListener w) {
        this.widgetsHandlingMouseClick.add(w);
    }

    public void handleAction(Widget w, EnumAction action) {
        if (message != null) {
            message.handleAction(w, action);
        }
    }

    public void buttonClicked(EnumAction action, int buttonId) {
        ScriptManager.callEvent(new EvtGUI.EvtButtonClicked(buttonId));
    }


    public void addKeyTypeListener(IKeyListener w) {
        this.widgetsHandlingKeyType.add(w);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (message != null) {
            message.keyTyped(typedChar, keyCode);
            return;
        } else {
            if (canBeClosed && keyCode == 1) {
                this.closeFrame();
            }
            for (IKeyListener w : widgetsHandlingKeyType) {
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
            for (IClickListener w : widgetsHandlingMouseClick) {
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
            } else if (w instanceof Container) {
                String stab = " ";
                for (int j = 0; j < tab; j++)
                    stab += "| ";
                System.out.println(
                        stab + "┗ " + " " + w.id + " " + (this.widgetsHandlingKeyType.contains(w) ? "[w] " : "")
                                + (w.toString().replaceAll("com.nicoszpako.rpworld.guiapi.", "")) + " ");
                showHierarchy(((Container) w).content, tab + 1);
            } else {
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
        //System.out.println("Number of widgets:"+ runningWidgets.size()+" "+blankWidgets.size());
        if (message != null) {
            message.drawScreen(mouseX, mouseY, partialTicks);
            GlStateManager.translate(0, 0, -250);
            mouseX = 0;
            mouseY = 0;
        }
        if (Minecraft.getMinecraft().currentScreen == this && !specialInteractionMode)
            this.isInteracting = false;
        if (!Mouse.isButtonDown(0))
            hold = false;
        tickExisted++;
        for (Widget w : runningWidgets) {
            w.draw(mouseX, mouseY);
        }
        if (isDrawToolTip()) {
            if (tooltipToDraw != null)
                drawToolTip(tooltipToDraw, mouseX, mouseY);
        }

    }

    public Widget getWidgetFromId(int id) {
        for (Widget w : runningWidgets) {
            if (w.getId() == id)
                return w;
        }
        return null;
    }

    public Widget getWidgetUnderMouse(int mouseX, int mouseY) {
        for (Widget w : runningWidgets) {
            if (w.isMouseOnWidget(mouseX, mouseY))
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


}
