package fr.nico.sqript.forge.gui;

public class DisplayInfos {

    public double x = 0, y = 0, scale = 1;
    public double width, height;
    public double u, v, u2, v2;

    public DisplayInfos(double x, double y, double width, double height, double u, double v, double u2, double v2) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }

    public DisplayInfos(double x, double y, double width, double height, double u, double v) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.u2 = 1;
        this.v2 = 1;
    }

    public DisplayInfos(double x, double y, double scale, double width, double height) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.u = 0;
        this.v = 0;
        this.u2 = 1;
        this.v2 = 1;
    }
}
