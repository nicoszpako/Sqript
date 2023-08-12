package fr.nico.sqript.forge.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public class TextBox extends TextField {
    private static final int enabledColor = 0xFFFFFFFF;
    private static final int disabledColor = 0xFFFFFFFF;


    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    public int incY = 4;
    private int lineScroll = 0;
    private int maxLines = 0;

    public TextBox(int width, int height) {
        super(width, height);
        this.style.width = width;
        this.style.height = height;

    }

    public TextBox(int width, int height, int maxStringLength) {
        super(width, height);
        this.style.width = width;
        this.style.height = height;
        this.setMaxStringLength(maxStringLength);
    }

    public TextBox(Style style) {
        super(style);
        this.style = style;
        this.setMaxStringLength((int) (Math.ceil((style.width - 5) / fontRenderer.getCharWidth('a')) * ((style.height - incY) / fontRenderer.FONT_HEIGHT) - 1));

    }

    private int getLineScrollOffset() {
        return 0;
    }

    public void advanceLine() {
        if (lineScroll < maxLines - 1) {
            lineScroll++;
        }
    }

    public void regressLine() {
        if (lineScroll > 0) {
            lineScroll--;
        }
    }

    public boolean moreLinesAllowed() {
        return fontRenderer.listFormattedStringToWidth(getCursoredText(), style.width).size() * fontRenderer.FONT_HEIGHT < style.height;
    }

    private String getCursoredText() {
        if (!isFocused()) {

            return getText();
        }

        int cursorPos = getCursorPosition() - getLineScrollOffset();
        String text = getText();
        if (cursorPos < 0) {
            return text;
        }
        if (cursorPos >= text.length()) {
            return text + "_";
        }
        return text.substring(0, cursorPos) + "_" + text.substring(cursorPos);
    }

    private void drawScrolledSplitString(String text, int startX, int startY, int width, int textColour) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
        int s = (this.style.height - 10) / fontRenderer.FONT_HEIGHT;
        maxLines = lines.size();
        int count = 0;
        int lineY = startY;
        if (lines.size() > 0)
            for (int i = lineScroll; i < lineScroll + s; i++) {
                if (i >= 0 && i < lines.size())
                    fontRenderer.drawStringWithShadow(lines.get(i), startX, lineY + i * fontRenderer.FONT_HEIGHT, textColour);

            }
		/*
		for (String line : lines) {
			if (count < lineScroll) {
				count++;
				continue;
			} else if (lineY + fontRenderer.FONT_HEIGHT - startY > style.height) {
				break;
			}

			lineY += fontRenderer.FONT_HEIGHT;

			count++;
		}
	*/
    }

    private void drawScrolledSplitStringNoShadow(String text, int startX, int startY, int width, int textColour) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);

        maxLines = lines.size();

        int count = 0;
        int lineY = startY;

        for (String line : lines) {
            if (count < lineScroll) {
                count++;
                continue;
            } else if (lineY + fontRenderer.FONT_HEIGHT - startY > style.height) {
                break;
            }

            fontRenderer.drawString(line, startX, lineY, textColour, false);
            lineY += fontRenderer.FONT_HEIGHT;

            count++;
        }

    }


    @Override
    public void draw(int a, int b) {

        if (!getVisible()) {
            return;
        }
        if (style.bordersize > 0) {
            drawBorders(this.style.bordercolor);
        }
        if (getEnableBackgroundDrawing()) {
            drawRect(x, y, x + this.style.width, y + this.style.height, this.style.backgroundcolor);
        }

        int textColour = isFocused() ? enabledColor : disabledColor;

        if (isFocused()) drawScrolledSplitString(getCursoredText(), x + 4, y + incY, style.width - 5, textColour);
        else {
            drawScrolledSplitString(getCursoredText() + "_", x + 4, y + incY, style.width - 5, 0xFFDEDEDE);
        }

    }


    @Override
    public void keyTyped(char c, int ke) {
        if (c == '\r') {
            String s = "\n";
            int i = this.getCursorPosition() < this.getSelectionEnd() ? this.getCursorPosition() : this.getSelectionEnd();
            int j = this.getCursorPosition() < this.getSelectionEnd() ? this.getSelectionEnd() : this.getCursorPosition();
            int k = this.getMaxStringLength() - this.getText().length() - (i - j);

            if (this.getCursorPosition() == this.getText().length()) this.setText(this.getText() + "\n");
            else {
                String s1 = this.getText().substring(0, this.getCursorPosition());
                String s2 = this.getText().substring(this.getCursorPosition());
                String total = s1 + s + s2;
                this.setText(total);
            }

            this.setResponderEntryValue(this.getId(), this.getText());


            return;
        } else
            super.keyTyped(c, ke);
    }
}
