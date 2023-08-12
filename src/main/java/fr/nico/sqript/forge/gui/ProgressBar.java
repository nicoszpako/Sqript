package fr.nico.sqript.forge.gui;

import org.lwjgl.opengl.GL11;

public class ProgressBar extends Widget{

	
	public int value,maxvalue;
	public ProgressBar(int width, int height, int maxvalue,int color) {
		this.style.width=width;
		this.style.height=height;
		this.style.backgroundcolor=color;
		this.maxvalue=maxvalue;
	}
	
	
	
	@Override
	public void draw(int mouseX, int mouseY) {
		GL11.glPushMatrix();
		super.draw(mouseX, mouseY);
		drawBorders(style.bordercolor);
		drawRect(this.x, this.y, (int) ((float)this.x+((float)this.style.width)*(float)((float)value/(float)maxvalue)), getY2(), style.backgroundcolor);
		GL11.glPopMatrix();
	}
	
}
