package fr.nico.sqript.forge.gui;

public class TestFrame extends Frame {
    int a = 0xFFFFFFFF;

    public TestFrame() {
        this.blankWidgets.clear();
        this.widgetsHandlingMouseWheel.clear();
        TabPane maincontainer = new TabPane(Widget.xFromCenter(-200), Widget.yFromCenter(-130), 400, 260);
        maincontainer.style.setBackgroundcolor(0xFF888888);
        maincontainer.style.setBordersize(1);
        maincontainer.style.setBordercolor(0xFF000000);


        ScrollPane scroll = new ScrollPane(400, 260);
        scroll.style.setBackgroundcolor(0xFFFF7777);
        scroll.style.setMargin(0, 3, 3, 0);
        scroll.style.setBordersize(1);
        scroll.style.setBordercolor(0xFF000000);


        TextBox tb = new TextBox(90, 95);
        tb.style.setMargin_top(0);
        tb.style.setBackgroundcolor(0xFF777777);

        scroll.addWidget(tb);


        maincontainer.addTab(scroll, "Page 1", false);

        for (int i = 2; i < 6; i++) {
            ScrollPane scroll2 = new ScrollPane(400, 260);
            scroll2.style.setBackgroundcolor(0xFFFF7777);
            scroll2.style.setMargin(0, 3, 3, 0);
            scroll2.style.setBordersize(1);
            scroll2.style.setBordercolor(0xFF000000);


            maincontainer.addTab(scroll2, "Page " + i, false);
        }

        maincontainer.setTabDisplayMode(TabPane.DISPLAY_UP);

        addRunningWidget(maincontainer);
    }


    @Override
    public void handleAction(Widget w, EnumAction action) {
        if (w instanceof Button && action == EnumAction.MOUSE_LEFT_CLICK) {
        }
    }


    void addPane(Container maincontainer) {

        for (int j = 0; j < 1; j++) {
            Container scrollpane = new Container(90, 150);
            scrollpane.style.setBordersize(1);
            scrollpane.style.setBackgroundcolor(0xFFAA8888);
            scrollpane.style.setDrawBackground(true);
            scrollpane.style.setBordercolor(0xFFFFFFFF);
            scrollpane.style.setMargin_top(3);
            scrollpane.style.setMargin_left(3);
            for (int i = 0; i < 50; i++) {
                Button mainButton = new Button(20, 20, String.valueOf(i)) {
                    @Override
                    public void drawBorders(int color) {
                        super.drawBorders(a);
                    }
                };
                mainButton.setDrawType(Button.DRAW_TEXT);
                mainButton.style.setBackgroundcolor(0xFF888888);
                mainButton.style.setMargin_left(3);
                mainButton.style.setMargin_top(3);

                scrollpane.addWidget(mainButton);

            }
            maincontainer.addWidget(scrollpane);
        }

    }
}
