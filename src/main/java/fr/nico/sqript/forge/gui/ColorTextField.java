package fr.nico.sqript.forge.gui;

public class ColorTextField extends TextField {

    public ColorTextField(int width, int height) {
        super(width, height);
        this.setText("\2477#\247fFFFFFF");
    }

    @Override
    public void draw(int a, int b) {
        // TODO Auto-generated method stub
        super.draw(a, b);
        try {
            drawRect(this.getX2() - 20, this.getY2(), this.getX2(), this.getY(), getColor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromCursor(int num) {
        if (getCursorPosition() > 5)
            super.deleteFromCursor(num);
    }

    public boolean isFilled() {
        return this.getText().length() == 11;
    }

    @Override
    public void writeText(String textToWrite) {
        if (textToWrite.equalsIgnoreCase("a") || textToWrite.equalsIgnoreCase("b") || textToWrite.equalsIgnoreCase("c") || textToWrite.equalsIgnoreCase("d") || textToWrite.equalsIgnoreCase("e") || textToWrite.equalsIgnoreCase("f") || textToWrite.equalsIgnoreCase("0") || textToWrite.equalsIgnoreCase("1") || textToWrite.equalsIgnoreCase("2") || textToWrite.equalsIgnoreCase("3") || textToWrite.equalsIgnoreCase("4") || textToWrite.equalsIgnoreCase("5") || textToWrite.equalsIgnoreCase("6") || textToWrite.equalsIgnoreCase("7") || textToWrite.equalsIgnoreCase("8") || textToWrite.equalsIgnoreCase("9"))
            if (this.getText().length() < 11) super.writeText(textToWrite.toUpperCase());
    }

    public int getColor() {
        return getText().isEmpty() ? 0xFF000000 : (int) Long.parseLong("FF" + this.getText().substring(5), 16);
    }
}
