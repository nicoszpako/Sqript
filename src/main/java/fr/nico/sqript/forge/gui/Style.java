package fr.nico.sqript.forge.gui;

public class Style {

    public DisplayInfos display;
    protected int padding_top = 0, padding_left = 0, padding_right = 0, padding_bottom = 0;
    protected int margin_left = 0, margin_top = 0, margin_right = 0, margin_bottom = 0;
    protected int width, height;
    protected float bordersize;
    protected int bordercolor = 0xFF000000, backgroundcolor = 0xFF000000;
    protected boolean drawBackground = false;
    protected boolean enabled = true;
    protected boolean drawborders;
    protected int hoverColor = 0xFF000000;
    protected int hoverBorderColor = 0xFF000000;


    public Style() {
    }

    public int getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public int getHoverBorderColor() {
        return hoverBorderColor;
    }

    public void setHoverBorderColor(int hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
    }

    public void setMargin(int left, int top, int right, int bottom) {
        setMargin_left(left);
        setMargin_top(top);
        setMargin_right(right);
        setMargin_bottom(bottom);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        setPadding_left(left);
        setPadding_top(top);
        setPadding_bottom(bottom);
        setPadding_right(right);
    }

    public DisplayInfos getDisplay() {
        return display;
    }

    public void setDisplay(DisplayInfos display) {
        this.display = display;
    }

    public int getPadding_top() {
        return padding_top;
    }

    public void setPadding_top(int padding_top) {
        this.padding_top = padding_top;
    }

    public int getPadding_left() {
        return padding_left;
    }

    public void setPadding_left(int padding_left) {
        this.padding_left = padding_left;
    }

    public int getPadding_right() {
        return padding_right;
    }

    public void setPadding_right(int padding_right) {
        this.padding_right = padding_right;
    }

    public int getPadding_bottom() {
        return padding_bottom;
    }

    public void setPadding_bottom(int padding_bottom) {
        this.padding_bottom = padding_bottom;
    }

    public int getMargin_left() {
        return margin_left;
    }

    public void setMargin_left(int margin_left) {
        this.margin_left = margin_left;
    }

    public int getMargin_top() {
        return margin_top;
    }

    public void setMargin_top(int margin_top) {
        this.margin_top = margin_top;
    }

    public int getMargin_right() {
        return margin_right;
    }

    public void setMargin_right(int margin_right) {
        this.margin_right = margin_right;
    }

    public int getMargin_bottom() {
        return margin_bottom;
    }

    public void setMargin_bottom(int margin_bottom) {
        this.margin_bottom = margin_bottom;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getBordersize() {
        return bordersize;
    }

    public void setBordersize(float bordersize) {
        this.bordersize = bordersize;
        if (bordersize > 0) this.setDrawborders(true);

    }

    public int getBordercolor() {
        return bordercolor;
    }

    public void setBordercolor(int bordercolor) {
        this.bordercolor = bordercolor;
        this.setDrawborders(true);
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public void setBackgroundcolor(int backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
        this.setDrawBackground(true);
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDrawborders() {
        return drawborders;
    }

    public void setDrawborders(boolean drawborders) {
        this.drawborders = drawborders;
    }
}
