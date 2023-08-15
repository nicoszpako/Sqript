package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;

public class Message extends Frame {

    public int id;
    public boolean setNull = true;
    public boolean updateFrameOnly = false;
    public NBTTagCompound nbt;
    public EnumMessageOptions mOptions = EnumMessageOptions.OK;
    public String[] message_text;
    public String title;
    protected Frame parentScreen;

    public Message() {
    }

    public Message(EnumMessageOptions mOptions, String message, String title) {
        this.mOptions = mOptions;
        this.message_text = new String[]{message};
        this.title = title;
    }

    public Message(EnumMessageOptions mOptions, String[] message, String title) {
        this.mOptions = mOptions;
        this.message_text = message;
        this.title = title;
    }

    public Message(EnumMessageOptions mOptions, String[] message, String title, int id) {
        this.mOptions = mOptions;
        this.message_text = message;
        this.title = title;
        this.id = id;
    }

    public Message(EnumMessageOptions mOptions, String message, String title, int id) {
        this.mOptions = mOptions;
        this.message_text = new String[]{message};
        this.title = title;
        this.id = id;
    }


    public Message setUpdateFrameOnly(boolean setUpdateFrameOnly, NBTTagCompound nbt) {
        this.updateFrameOnly = setUpdateFrameOnly;
        this.nbt = nbt;
        return this;
    }

    public Frame getParentScreen() {
        return parentScreen;
    }

    public void setParentScreen(Frame parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void addWidgets() {
        int height = 15 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * message_text.length + 75;
        //Container main = new Container(Widget.xFromCenter(-100),Widget.yFromCenter(-height/2),200,Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT*message.length+65);
        Container main = FrameUtils.generateMainContainer(Styles.nullStyle(200, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * message_text.length + 65));

        main.style.setBackgroundcolor(0xEF222222);
        main.style.setDrawBackground(true);

        Container titlec = new Container(190, 15);
        titlec.style.setBackgroundcolor(0x99111111);
        titlec.style.setMargin(5, 5, 0, 0);
        Label ltitle = new Label(title, 0xFF44DD44, 1);
        ltitle.style.setMargin(45, ltitle.style.getHeight() / 2, 0, 0);
        titlec.setLayout(EnumLayout.BLOCK_CENTER);
        titlec.addWidget(ltitle);

        Container descc = new Container(190, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * message_text.length + 10);
        descc.style.setBackgroundcolor(0x99111111);
        descc.style.setMargin(5, 5, 0, 0);
        descc.style.setPadding(5, 5, 0, 0);
        descc.setLayout(EnumLayout.BLOCK);
        for (String s : message_text) {
            Label m = new Label(s, 0xFFFFFFFF, 1);
            descc.addWidget(m);

        }

        Button pay = new Button(190, 20);
        pay.style.setBackgroundcolor(0xFFAAAAAA);
        pay.style.setMargin(5, 5, 0, 0);
        pay.setHoverColor(0xFF888888);
        pay.setDisplayText("Valider");
        pay.setDisplayTextColor(0xFFFFFFFF);

        main.addWidget(titlec);

        main.addWidget(descc);
        main.addWidget(pay);
        this.addRunningWidget(main);
    }

    @Override
    protected void keyTyped(char c, int key) {
        for (IKeyListener w : widgetsHandlingKeyType) {
            w.keyTyped(c, key);

        }
        if (key == Keyboard.KEY_ESCAPE) {
            if (nbt != null) parentScreen.update(nbt);
            parentScreen.message = null;
        }
    }

    public MessageFloat getMessageFloat() {
        if (this instanceof MessageFloat) {
            return (MessageFloat) this;
        } else return null;
    }

    public MessageString getMessageString() {
        if (this instanceof MessageString) {
            return (MessageString) this;
        } else return null;
    }

    @Override
    public void handleAction(Widget w, EnumAction action) {
        if (updateFrameOnly) {
            parentScreen.update(nbt);
        }

        if (((Button) w).getDisplayText().equals("Valider"))
            parentScreen.messageResponse(this, EnumMessageOptions.PAY);
        if (setNull) parentScreen.message = null;


    }

    public Message setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 200);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

}
