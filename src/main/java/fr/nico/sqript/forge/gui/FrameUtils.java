package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class FrameUtils {

    public static Container generateTextFieldWithLabel(TextField t, int maxWidth, String label, float scale, boolean borderTest) {
        Container s = new Container(maxWidth, t.style.height);
        s.setLayout(EnumLayout.FLOW);
        LabelArea l = new LabelArea(label, 0xFFFFFFFF, scale, maxWidth - t.style.getWidth() - 2 * (int) t.style.bordersize);
        l.style.setMargin(0, 5, 0, 0);
        s.addWidget(l);
        s.addWidget(t);
        if (borderTest) {
            s.style.setBordercolor(0xFF000000);
            s.style.setBordersize(1);
        }
        return s;
    }



    public static Container generateTextFieldWithLabel(TextField t, int maxWidth, String label, boolean borderTest) {
        Container s = new Container(maxWidth, t.style.height);
        s.setLayout(EnumLayout.FLOW);
        LabelArea l = new LabelArea(label, 0xFFFFFFFF, 1, maxWidth - t.style.getWidth() - 2 * (int) t.style.bordersize);
        l.style.setMargin(0, 5, 0, 0);
        s.addWidget(l);
        s.addWidget(t);
        if (borderTest) {
            s.style.setBordercolor(0xFF000000);
            s.style.setBordersize(1);
        }
        return s;
    }

    public static Container generateDropFieldWithLabel(DropSelection t, int maxWidth, String label, boolean borderTest) {
        Container s = new Container(maxWidth, t.style.height);
        s.setLayout(EnumLayout.FLOW);
        LabelArea l = new LabelArea(label, 0xFFFFFFFF, 1, maxWidth - t.style.getWidth() - 2 * (int) t.style.bordersize);
        float y = l.style.height;
        float x = t.style.height;
        l.style.setMargin(0, (int) (y / 2 + (x / 2 - y)) + 1, 0, 0);
        s.addWidget(l);
        s.addWidget(t);
        if (borderTest) {
            s.style.setBordercolor(0xFF000000);
            s.style.setBordersize(1);
        }
        return s;
    }

    public static Container generateMainContainer(Style s) {
        Container c = new Container(Widget.xFromCenter(-s.width / 2), Widget.yFromCenter(-s.height / 2), s.width, s.height);
        c.setStyle(s);
        return c;
    }

    public static ScrollPane generateMainScrollPane(Style s) {
        ScrollPane c = new ScrollPane(Widget.xFromCenter(-s.width / 2), Widget.yFromCenter(-s.height / 2), s.width, s.height);
        c.setStyle(s);
        return c;
    }

    public static Container generateTextAreaWithLabel(TextBox t, int maxWidth, String label, boolean borderTest) {
        Container s = new Container(maxWidth, t.style.height);
        s.setLayout(EnumLayout.FLOW);
        LabelArea l = new LabelArea(label, 0xFF, 1, maxWidth - t.style.getWidth() - 2 * (int) t.style.bordersize);
        l.style.setMargin(0, 5, 0, 0);
        s.addWidget(l);
        s.addWidget(t);
        if (borderTest) {
            s.style.setBordercolor(0xFF000000);
            s.style.setBordersize(1);
        }
        return s;
    }

    public static void openFrame(Frame frame) {
        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(frame));
    }

}
