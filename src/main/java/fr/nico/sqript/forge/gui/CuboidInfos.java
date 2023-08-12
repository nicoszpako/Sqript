package fr.nico.sqript.forge.gui;

public class CuboidInfos {
    int x, y, width, height;

    public CuboidInfos(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isMouseOn(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y
                && mouseY <= this.y + this.height;
    }
}
