package fr.nico.sqript.forge.gui;

import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.math.NumberUtils;

public class PasswordField extends TextField {


    boolean onlyNumbers = false;


    public PasswordField(int width) {
        super(width);

    }

    @Override
    public void writeText(String textToWrite) {
        if (onlyNumbers) {
            if (NumberUtils.isCreatable(textToWrite)) super.writeText(textToWrite);
        } else {
            super.writeText(textToWrite);

        }
    }

    public boolean isOnlyNumbers() {
        return onlyNumbers;
    }

    public void setOnlyNumbers(boolean onlyNumbers) {
        this.onlyNumbers = onlyNumbers;
    }


    @Override
    public void draw(int a, int b) {
        if (style.isDrawBackground()) {
            drawBorders(style.bordercolor);
            drawRect(this.x, this.y, this.x + this.style.width, this.y + this.style.height, this.style.backgroundcolor);
        }

        int i = this.isEnabled ? this.enabledColor : this.disabledColor;
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        String coded = "";
        for (int p = 0; p < text.length(); p++) {
            coded += "*";

        }
        String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.style.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
        int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
        int i1 = this.enableBackgroundDrawing ? this.y + (this.style.height - 8) / 2 : this.y;
        int j1 = l;

        if (k > s.length()) {
            k = s.length();
        }

        if (!s.isEmpty()) {
            String s1 = flag ? coded.substring(0, j) : s;
            j1 = this.fontRenderer.drawStringWithShadow(s1, (float) l, (float) i1, i);
        }

        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
        int k1 = j1;

        if (!flag) {
            k1 = j > 0 ? l + this.style.width : l;
        } else if (flag2) {
            k1 = j1 - 1;
            --j1;
        }

        if (!s.isEmpty() && flag && j < s.length()) {
            j1 = this.fontRenderer.drawStringWithShadow(coded.substring(j), (float) j1, (float) i1, i);
        }

        if (flag1) {
            if (flag2) {
                Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
            } else {
                this.fontRenderer.drawStringWithShadow("_", (float) k1, (float) i1, i);
            }
        }

        if (k != j) {
            int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
            this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
        }

    }
}
