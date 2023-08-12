package fr.nico.sqript.forge.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Image extends Widget {

	public String imageName;
	public ResourceLocation image;
	public boolean modal=false;
	public boolean useUvs;
	public double[] Uvs = new double[3];
	public Image(String imageName,int width,int height) {
		this.style.width=width;
		this.style.height=height;
		this.imageName =imageName;
		image=new ResourceLocation(imageName);
	}
	
	@Override
	public void draw(int mouseX, int mouseY) {
		GL11.glPushMatrix();

		drawBorders(this.style.getBordercolor());
		super.draw(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		if(modal){
			this.drawTexturedModalRect(x, y, (int)Uvs[0], (int)Uvs[1],this.style.width, this.style.height);
		}else if(useUvs){
			this.drawTexturedRect((int)x,(int)y, this.style.width, this.style.height, Uvs[0],Uvs[1],Uvs[2],Uvs[3]);
		}else{
			this.drawTexturedRect((int)x,(int)y, this.style.width, this.style.height);

		}
		GlStateManager.resetColor();

		GL11.glPopMatrix();

	}
	
	public static void drawTexturedRect(double x, double y, double w, double h)
	{
		drawTexturedRect(x, y, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
	}



	
}
