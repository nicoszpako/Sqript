package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Container extends Widget implements IClickListener, IKeyListener, IMouseWheelListener {

    public static final int DRAW_COLOR = 0;
    public static final int DRAW_IMAGE = 1;
    public static final int DRAW_IMAGE_FIXED = 2;
    public ResourceLocation imageSource;
    public boolean drawHoverColor = false;

    public boolean isAdaptScissorBoxToParent() {
        return adaptScissorBoxToParent;
    }

    public void setAdaptScissorBoxToParent(boolean adaptScissorBoxToParent) {
        this.adaptScissorBoxToParent = adaptScissorBoxToParent;
    }

    public boolean adaptScissorBoxToParent = true;
    public DisplayInfos display;
    public int draw_type = 0;
    public boolean clickEnabled = false;
    protected List<Widget> content = new ArrayList<Widget>();
    protected EnumLayout layout = EnumLayout.FLOW;
    List<Widget> widgetsHandlingMouseClick = new ArrayList<Widget>();
    List<Widget> widgetsHandlingMouseWheel = new ArrayList<Widget>();
    List<Widget> widgetsHandlingKeyType = new ArrayList<Widget>();
    int mtop = 0;
    int mleft = 0, mright = 0;
    int bestHeight = 0;
    private final boolean canHold = false;
    private final boolean hover = false;
    private final boolean hold = false;

    public Container(int width, int height) {
        this.style.width = width;
        this.style.height = height;
    }

    public Container(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.style.width = width;
        this.style.height = height;

    }

    public Container(int left, int top, int width, int height, int backgroundColor) {
        this.x = left;
        this.y = top;
        this.style.width = width;
        this.style.height = height;
        super.style.backgroundcolor = backgroundColor;
        style.drawBackground = true;
    }

    public Container(Style style) {
        this.style = style;
    }

    public void clearWidgets() {
        this.content.clear();
    }

    public void addWidget(Widget w) {
        w.setId(content.size());
        w.setParent(this);
        w.onAddToContainer(this);
        content.add(w);

    }

    public void addWidget(int index, Widget w) {
        w.setId(content.size());
        w.setParent(this);
        w.onAddToContainer(this);
        content.add(index, w);

    }

    public List<Widget> getContent() {
        return content;
    }

    public void removeWidget(Widget w) {
        content.remove(w);
    }

    public void removeWidget(int index) {
        content.remove(index);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (!this.style.isEnabled()) return;
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

            if (style.drawBackground) {

                if (draw_type == DRAW_COLOR)
                    if (drawHoverColor) {
                        if (!this.isMouseOnWidget(mouseX, mouseY))
                            drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);
                        else if (style.hoverColor != 0)
                            drawRect(x, y, x + style.width, y + style.height, style.hoverColor);
                        else
                            drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);
                    } else {
                        drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);

                    }

                if (draw_type == DRAW_IMAGE) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(imageSource);
                    drawTexturedModalRect(this.x, this.y, 0, 0, (int) display.width,
                            (int) display.height);
                }
                if (draw_type == DRAW_IMAGE_FIXED) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(imageSource);
                    drawTexturedRect(this.x, this.y, (int) display.width, (int) display.height);
                }
            }

            for (int i = 0; i < content.size(); i++) {

                Widget w = content.get(i);
                Widget prev = null;
                if (i > 0)
                    prev = content.get(i - 1);

                if (layout == EnumLayout.FLOW) {

                    /*
                     * System.out.println("--------------"); if (w instanceof Button)
                     * System.out.println(((Button) w).getDisplayText());
                     * System.out.println("container id " + this.id);
                     * System.out.println("width container " + this.width); System.out.println("i" +
                     * i); System.out.println("width space (mleft+...)" + (mleft + w.getWidth() +
                     * w.margin_left + 2 * w.bordersize + mright)); System.out.println("widget : " +
                     * w.toString()); System.out.println("mleft " + mleft + " mtop " + mtop +
                     * " bestheight "+bestHeight); System.out.println("widget infos " + w.getWidth()
                     * + " " + w.margin_left + " " + w.bordersize); System.out.println("condition "
                     * + (mleft + w.getWidth() + w.margin_left + 2 * w.bordersize + mright >
                     * this.width - this.padding_right));
                     */
                    if (!w.isRelativeFixed) {
                        if (w.style.getHeight() + w.style.margin_top + 2 * w.style.bordersize > bestHeight) {
                            bestHeight = i > 0
                                    ? content.get(i - 1).style.getHeight() + content.get(i - 1).style.margin_top
                                    + 2 * (int) content.get(i - 1).style.bordersize
                                    : w.style.getHeight() + w.style.margin_top + 2 * (int) w.style.bordersize;
                        }
                        if ((mleft + w.style.getWidth() + w.style.margin_left + 2 * w.style.bordersize
                                + mright > this.style.width - this.style.padding_right)) {

                            mtop += bestHeight;
                            bestHeight = 0;
                            mleft = 0;
                            mright = 0;
                        } else {
                            mright += w.style.margin_right;
                        }
                        w.setY(this.y + mtop + w.style.margin_top + (int) w.style.bordersize + this.style.padding_top);

                        w.setX(this.x + mleft + w.style.margin_left + mright + this.style.padding_left);
                        mleft += w.style.getWidth() + w.style.margin_left + 2 * w.style.bordersize;
                    } else {
                        w.setX(this.x + this.style.padding_left + w.fixed_x);
                        w.setY(this.y + this.style.padding_top + w.fixed_y);
                    }
                    w.draw(mouseX, mouseY);
                } else if (layout == EnumLayout.NONE) {
                    w.draw(mouseX, mouseY);

                } else if (layout == EnumLayout.BLOCK) {
                    if (!w.isRelativeFixed) {

                        w.x = this.x + w.style.margin_left + this.style.padding_left;
                        w.y = this.y + (int) w.style.bordersize + w.style.margin_top + mtop + this.style.padding_top;
                        mtop += 2 * w.style.bordersize + w.style.margin_top + w.style.height + w.style.margin_bottom;
                    } else {
                        w.setX(this.x + w.fixed_x);
                        w.setY(this.y + w.fixed_y);
                    }
                    w.draw(mouseX, mouseY);
                } else if (layout == EnumLayout.BLOCK_CENTER) {
                    if (!w.isRelativeFixed) {
                        w.x = this.x + ((this.style.width / 2) - (w.style.width / 2));
                        w.y = this.y + (int) w.style.bordersize + w.style.margin_top + mtop + this.style.padding_top;
                        mtop += 2 * w.style.bordersize + w.style.margin_top + w.style.height + w.style.margin_bottom;
                    } else {
                        w.setX(this.x + w.fixed_x);
                        w.setY(this.y + w.fixed_y);
                    }
                    w.draw(mouseX, mouseY);
                }

            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

        }
        mleft = 0;
        mtop = 0;
        mright = 0;
        GlStateManager.popMatrix();
    }

    public EnumLayout getLayout() {
        return layout;
    }

    public void setLayout(EnumLayout layout) {
        this.layout = layout;
    }

    public void addMouseInputListener(Widget w) {
        this.widgetsHandlingMouseWheel.add(w);
    }

    @Override
    public void onAddToContainer(Container container) {
        super.onAddToContainer(container);

        container.addMouseInputListener(this);
        container.addMouseClickListener(this);
        container.addKeyTypeListener(this);
    }

    @Override
    public void onAddToFrame(Frame frame) {
        super.onAddToFrame(frame);
        frame.addMouseInputListener(this);
        frame.addMouseClickListener(this);
        frame.addKeyTypeListener(this);

    }

    @Override
    public void mouseWheelInput(int i) {
        super.mouseWheelInput(i);
        for (Widget e : this.widgetsHandlingMouseWheel) {
            e.mouseWheelInput(i);
        }
    }

    public void addMouseClickListener(Widget w) {
        this.widgetsHandlingMouseClick.add(w);
    }

    @Override
    public void mouseClick(int x, int y, int mousebutton) {
        super.mouseClick(x, y, mousebutton);

        for (Widget w : widgetsHandlingMouseClick) {
            w.mouseClick(x, y, mousebutton);
        }


    }

    public void checkClick(int mouseX, int mouseY) {

    }

    public void onClick(EnumAction mouseLeftClick) {

    }

    public void addKeyTypeListener(Widget w) {
        this.widgetsHandlingKeyType.add(w);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

        super.keyTyped(typedChar, keyCode);
        for (Widget w : widgetsHandlingKeyType) {
            w.keyTyped(typedChar, keyCode);

        }
    }

    public Widget getWidgetFromId(int id) {
        for (Widget w : content) {
            if (w.getId() == id)
                return w;
        }
        return null;
    }

    public DisplayInfos getDisplay() {
        return display;
    }

    public void setDisplay(DisplayInfos display) {
        this.display = display;
    }

    public ResourceLocation getImageSource() {
        return imageSource;
    }

    public void setImageSource(ResourceLocation imageSource) {
        this.imageSource = imageSource;
    }

    public int getDrawType() {
        return draw_type;
    }

    public void setDrawType(int draw_type) {
        this.draw_type = draw_type;
    }

}
