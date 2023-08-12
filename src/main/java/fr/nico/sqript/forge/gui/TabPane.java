package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class TabPane extends Container {

    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_UP = 1;
    public List<Container> tabs = new ArrayList<Container>();
    private final List<Button> tabsButton = new ArrayList<Button>();
    private int selectedTab = 0;
    private int tabDisplayMode = 0;
    private int tabsBackgroundColor = 0xFF787878;
    private int tabButtonWidth = 80;
    private int tabButtonHeight = 20;
    private boolean containerViewOnly;

    public TabPane(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public TabPane(int width, int height) {
        super(width, height);

    }

    public TabPane(Style style) {
        super(style);
    }

    public int getTabButtonHeight() {
        return tabButtonHeight;
    }

    public void setTabButtonHeight(int tabButtonHeight) {
        this.tabButtonHeight = tabButtonHeight;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (tabs.size() > 0)
            tabs.get(selectedTab).keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseWheelInput(int i) {
        if (tabs.size() > 0)
            tabs.get(selectedTab).mouseWheelInput(i);
    }

    @Override
    public void mouseClick(int x, int y, int mousebutton) {
        if (tabs.size() > 0)
            tabs.get(selectedTab).mouseClick(x, y, mousebutton);
        for (Button b : tabsButton) {
            if (b.isMouseOnWidget(x, y))
                selectedTab = b.getId();
        }
    }

    public void addTab(Container cont, String name, boolean startFromRight) {
        tabs.add(cont);
        cont.onAddToContainer(this);
        Button b = new Button(tabButtonWidth, tabButtonHeight) {
            @Override
            public void drawBorders(int color) {
                if ((int) style.bordersize > 0 && style.drawborders) {
                    if (tabDisplayMode == DISPLAY_LEFT) {
                        drawRect(this.x - (int) style.bordersize, this.y - (int) style.bordersize, this.x, getY2(), color);
                        drawRect(this.x - (int) style.bordersize, getY2() + (int) style.bordersize, getX2() + (int) style.bordersize, getY2(), color);
                        drawRect(this.x - (int) style.bordersize, getY(), getX2() + (int) style.bordersize, getY() - (int) style.bordersize, color);

                    } else {

                        drawRect(this.x - (int) style.bordersize, this.y - (int) style.bordersize, this.x, getY2(), color);
                        drawRect(this.x - (int) style.bordersize, getY(), getX2() + (int) style.bordersize, getY() - (int) style.bordersize, color);
                        drawRect(getX2(), getY2() + (int) style.bordersize, getX2() + (int) style.bordersize, this.y - (int) style.bordersize, color);

                    }
                }
            }

            @Override
            public void draw(int mouseX, int mouseY) {

                if (selectedTab == this.getId()) {
                    if ((int) style.bordersize > 0)
                        drawBorders(this.style.hoverBorderColor);
                    drawRect(x, y, x + style.width, y + style.height + (TabPane.this.tabDisplayMode == DISPLAY_UP ? 1 : 0), tabsBackgroundColor);

                } else {
                    if (isMouseOnWidget(mouseX, mouseY)) {

                        if ((int) style.bordersize > 0)
                            drawBorders(this.style.hoverBorderColor);

                        drawRect(x, y, x + style.width, y + style.height, style.hoverColor);

                    } else {
                        if ((int) style.bordersize > 0)
                            drawBorders(style.bordercolor);

                        drawRect(x, y, x + style.width, y + style.height, style.backgroundcolor);

                    }
                }
                if (Mouse.isButtonDown(0) && isMouseOnWidget(mouseX, mouseY)) {
                    if (!hold) {
                        onClick(EnumAction.MOUSE_LEFT_CLICK);
                        hold = true;
                    }
                } else {
                    hold = false;
                }

                switch (drawType) {
                    case 0:
                        if (displayText != null)
                            drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayText,
                                    this.x + this.style.width / 2,
                                    this.y + this.style.height / 2 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2,
                                    displayTextColor);
                        break;
                    case 1:
                        if (resourceLocation != null)
                            drawIcon(resourceLocation);
                        break;
                    case 2:
                        if (itemstack != null)
                            drawItem(itemstack);
                        break;
                }

            }
        };
        if (startFromRight) b.setCommand("r");
        b.setDrawType(Button.DRAW_TEXT);
        b.setDisplayText(name);
        b.setId(tabsButton.size());
        b.setDisplayTextColor(0xFFFFFFFF);
        b.setHoverColor(0xFF333333);
        b.style.setBackgroundcolor(tabsBackgroundColor);
        b.style.setBordersize(this.style.bordersize);
        b.style.setBordercolor(this.style.bordercolor);
        b.setHoverBorderColor(0xFF000000);
        tabsButton.add(b);
    }

    @Override
    public void drawBorders(int color) {
        if ((int) style.bordersize > 0 && style.drawborders && !containerViewOnly) {


            if (tabDisplayMode == DISPLAY_LEFT) {
                int i = selectedTab + 1;


                int bheight = i * 20 - (int) this.style.bordersize + i * 3;
                int b1height = 20 - (int) this.style.bordersize + 3;

                drawRect(this.x + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0) - (int) style.bordersize, this.y - (int) style.bordersize + bheight, this.x + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY2(), color);

                drawRect(this.x - (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), this.y - (int) style.bordersize, this.x + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), this.y - (int) style.bordersize + bheight - b1height, color);

                drawRect(this.x - (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY2() + (int) style.bordersize, getX2() + (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY2(), color);
                drawRect(this.x - (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY(), getX2() + (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY() - (int) style.bordersize, color);
                drawRect(getX2() + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), getY2() + (int) style.bordersize, getX2() + (int) style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), this.y - (int) style.bordersize, color);
            } else {
                int i = selectedTab;

                int bheight = i * tabButtonWidth + i * 3 + 2;


                drawRect(this.x - (int) style.bordersize, this.y - (int) style.bordersize, this.x, getY2(), color);
                drawRect(this.x - (int) style.bordersize, getY2() + (int) style.bordersize, getX2() + (int) style.bordersize, getY2(), color);

                drawRect(this.x - (int) style.bordersize, getY(), this.x - (int) style.bordersize + bheight, getY() - (int) style.bordersize, color);
                drawRect(this.x - (int) style.bordersize + bheight + tabButtonWidth, getY(), getX2(), getY() - (int) style.bordersize, color);

                drawRect(getX2(), getY2() + (int) style.bordersize, getX2() + (int) style.bordersize, this.y - (int) style.bordersize, color);
            }
        }

    }

    @Override
    public void draw(int mouseX, int mouseY) {
        drawBorders(style.bordercolor);
        GlStateManager.pushMatrix();
        {
            if (!containerViewOnly) {
                GL11.glScissor(x * scaledResolution.getScaleFactor(), mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor(), (style.width + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0)) * scaledResolution.getScaleFactor(), style.height * scaledResolution.getScaleFactor());
                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                if (style.drawBackground) {
                    drawRect(x + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0), y, getX2(), getY2(), style.backgroundcolor);
                }

            }

            if (tabs.size() > 0) {

                Container cont = tabs.get(selectedTab);
                cont.setX(this.x + (containerViewOnly ? 0 : (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0)));
                cont.setY(this.y);
                cont.draw(mouseX, mouseY);

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                if (!containerViewOnly) {
                    for (int i = 0; i < tabsButton.size(); i++) {
                        if (tabDisplayMode == DISPLAY_LEFT) {

                            Button b = tabsButton.get(i);
                            b.setX(this.x - b.style.width - (int) this.style.bordersize + (this.getTabDisplayMode() == DISPLAY_LEFT ? this.tabButtonWidth / 2 : 0));
                            b.setY(this.y + i * b.style.height - (int) this.style.bordersize + i * 3 + (int) b.style.bordersize);
                            if (i == selectedTab) {

                                b.style.backgroundcolor = this.tabs.get(selectedTab).style.backgroundcolor;
                                drawRect(b.getX2(), b.getY(), b.getX2() + (int) b.style.bordersize, b.getY2() + (int) b.style.bordersize,
                                        b.style.getBackgroundcolor());

                            } else {
                                b.style.setBackgroundcolor(tabsBackgroundColor);
                            }
                            b.draw(mouseX, mouseY);

                        } else {

                            Button b = tabsButton.get(i);
                            b.setY(this.y - b.style.height - (int) b.style.bordersize);
                            if (!b.getCommand().equals("r")) b.setX(this.x + i * b.style.width + i * 3);
                            else b.setX(this.x + this.style.width - b.style.width);
                            if (i == selectedTab) {
                                b.style.backgroundcolor = this.tabs.get(selectedTab).style.backgroundcolor;
                                drawRect(b.getX(), b.getY2(), b.getX2() + (int) b.style.bordersize, b.getY2() + (int) b.style.bordersize,
                                        b.style.getBackgroundcolor());

                            } else
                                b.style.setBackgroundcolor(this.tabsBackgroundColor);
                            b.draw(mouseX, mouseY);
                            b.draw(mouseX, mouseY);
                        }
                    }
                }

            }

        }
        GlStateManager.popMatrix();
    }

    public int getTabDisplayMode() {
        return tabDisplayMode;
    }

    public void setTabDisplayMode(int tabDisplayMode) {
        this.tabDisplayMode = tabDisplayMode;
    }

    public int getTabsBackgroundColor() {
        return tabsBackgroundColor;
    }

    public void setTabsBackgroundColor(int tabsBackgroundColor) {
        this.tabsBackgroundColor = tabsBackgroundColor;
    }

    public int getTabButtonWidth() {
        return tabButtonWidth;
    }

    public void setTabButtonWidth(int tabButtonWidth) {
        this.tabButtonWidth = tabButtonWidth;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }


    /**
     * N'afficher que le container de la page sélectionnée
     */
    public void setContainerViewOnly(boolean b) {
        this.containerViewOnly = b;

    }

}
