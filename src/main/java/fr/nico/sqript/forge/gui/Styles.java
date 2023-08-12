package fr.nico.sqript.forge.gui;


public final class Styles {

    public static Style basic(int width, int height) {
        Style style = new Style();
        style.setBackgroundcolor(0xFF888888);
        style.setBordersize(1);
        style.setBordercolor(0xFF000000);
        style.setWidth(width);
        style.setHeight(height);
        style.setHoverColor(0xFF555555);
        return style;
    }

    public static Style modern(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF111111);
        return style;
    }

    public static Style darknetButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF274E0E);
        style.setHoverColor(0xFF4E9B1B);
        return style;
    }

    public static Style containerTopbar(int width, int height) {
        Style style = new Style();
        style.setBackgroundcolor(0x88888888);
        style.setWidth(width);
        style.setHeight(height);
        return style;
    }

    public static Style deleteButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFFf28787);
        style.setHoverColor(0xFFf2a9a9);
        return style;
    }

    public static Style validateButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF3399CC);
        style.setHoverColor(0xFF336699);
        return style;
    }

    public static Style disabledButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF999999);
        style.setHoverColor(0xFF999999);
        return style;
    }

    public static Style greenButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF4cc445);
        style.setHoverColor(0xFF5ae851);
        return style;
    }

    public static Style blueButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFF4482ff);
        style.setHoverColor(0xFF628adb);
        return style;
    }

    public static Style goldButton(int width, int height) {
        Style style = basic(width, height);
        style.setBackgroundcolor(0xFFFFCC00);
        style.setHoverColor(0xFFCC9900);
        return style;
    }

    public static Style nullStyle(int width, int height) {
        Style style = new Style();
        style.setBackgroundcolor(0x00);
        style.setHoverColor(0x00);
        style.setBordersize(0);
        style.setBordercolor(0x00);
        style.setWidth(width);
        style.setHeight(height);
        return style;
    }

}
