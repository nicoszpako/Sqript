package fr.nico.sqript.forge.gui;

import org.lwjgl.opengl.GL11;

public class Rectangle extends Widget{


	
	public Rectangle(int width, int height, int color) {
		this.style.width=width;
		this.style.height=height;
		this.style.backgroundcolor=color;
		
	}
	
	@Override
	public void draw(int mouseX, int mouseY) {
		GL11.glPushMatrix();

		if(this.getParent()!=null){
			GL11.glScissor(getHighestParent().x*scaledResolution.getScaleFactor(), mc.displayHeight - (getHighestParent().y + getHighestParent().style.height) * scaledResolution.getScaleFactor(),getHighestParent().style.width * scaledResolution.getScaleFactor(), getHighestParent().style.height * scaledResolution.getScaleFactor());

		}
		else{
			GL11.glScissor(x*scaledResolution.getScaleFactor()-style.bordersize*scaledResolution.getScaleFactor(), mc.displayHeight - (y + style.height) * scaledResolution.getScaleFactor()-style.bordersize*scaledResolution.getScaleFactor(), style.width * scaledResolution.getScaleFactor()+2*style.bordersize*scaledResolution.getScaleFactor(), style.height * scaledResolution.getScaleFactor()+2*style.bordersize*scaledResolution.getScaleFactor());
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		super.draw(mouseX, mouseY);
		drawBorders(style.bordercolor);
		drawRect(this.x, this.y, getX2(), getY2(), style.backgroundcolor);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		GL11.glPopMatrix();
	}
	
}
