package fr.nico.sqript.forge.gui;


public class MessageFloat extends Message {
    public String displayValue;
    public float value;
    TextField t;

    public MessageFloat(String title, String displayValue) {
        super();
        this.title = title;
        this.displayValue = displayValue;
    }

    public MessageFloat(String title, String displayValue, boolean b) {
        super();
        this.title = title;
        this.displayValue = displayValue;
        this.setNull = b;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void addWidgets() {
        this.blankWidgets.clear();
        t = new TextField(Styles.basic(145, 15));
        t.setOnlyNumbers(true);
        t.style.setMargin(0, 5, 0, 0);
        Container m = FrameUtils.generateMainContainer(Styles.modern(149, 69));
        m.style.setPadding(2, 2, 0, 0);
        m.setLayout(EnumLayout.BLOCK);
        m.addWidget(new Label(title, 0xFFFFFFFF, 0.85));
        m.addWidget(new Return(10));
        m.addWidget(new Label(displayValue, 0xFFFFFFFF, 0.8));
        m.addWidget(t);
        m.addWidget(new Return(1));

        Container bc = new Container(Styles.nullStyle(150, 16));

        Button d = new Button(Styles.greenButton(145, 15));
        d.setDisplayText("Valider");
        d.style.setMargin(0, 2, 0, 0);
        bc.addWidget(d);


        m.addWidget(bc);
        this.addRunningWidget(m);
    }


    @Override
    public void handleAction(Widget w, EnumAction action) {
        if (t != null && !t.getText().isEmpty()) {
            value = Float.parseFloat(t.getText());
        } else
            value = 0;
        super.handleAction(w, action);
    }
}
