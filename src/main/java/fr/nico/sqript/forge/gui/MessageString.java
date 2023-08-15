package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;

public class MessageString extends Message {
    public String title;
    public String displayValue;
    public String value;
    public int maxLength = 1024;
    TextField t;

    public MessageString(String title, String displayValue) {
        super();
        this.title = title;
        this.displayValue = displayValue;
    }

    public MessageString(String title, String displayValue, int maxLength) {
        super();
        this.title = title;
        this.displayValue = displayValue;
        this.maxLength = maxLength;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void addWidgets() {
        this.blankWidgets.clear();
        t = new TextField(Styles.basic(146, 15));
        t.setOnlyNumbers(false);
        t.setMaxStringLength(maxLength);
        t.style.setMargin(0, 5, 0, 0);
        Container m = FrameUtils.generateMainContainer(Styles.modern(150, 80));
        m.style.setPadding(2, 2, 0, 0);
        m.setLayout(EnumLayout.BLOCK);
        m.addWidget(new Label(title, 0xFFFFFFFF, 0.85));
        m.addWidget(new Return(10));
        m.addWidget(new Label(displayValue, 0xFFFFFFFF, 0.8));
        m.addWidget(t);
        m.addWidget(new Return(8));

        Container bc = new Container(Styles.nullStyle(150, 16));

        Button d = new Button(Styles.greenButton(146, 15));

        d.setDisplayText("Valider");
        d.style.setMargin(0, 2, 0, 0);
        bc.addWidget(d);


        m.addWidget(bc);
        this.addRunningWidget(m);
    }


    @Override
    public void handleAction(Widget w, EnumAction action) {
        if (t != null && !t.getText().isEmpty()) {
            value = t.getText();
            Minecraft.getMinecraft().displayGuiScreen(parentScreen);
            parentScreen.messageResponse(this, EnumMessageOptions.YES);
        }
    }
}
