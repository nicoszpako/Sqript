package fr.nico.sqript.forge.gui;

import org.lwjgl.opengl.GL11;

import com.flansmod.client.handlers.FlansModResourceHandler;
import com.flansmod.client.model.ModelVehicle;
import com.flansmod.client.model.RenderVehicle;
import com.flansmod.common.driveables.ItemVehicle;
import com.flansmod.common.driveables.VehicleType;
import com.nicoszpako.orycial.common.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class Renderer extends Widget{
	
	EnumRenderType renderType;
	float scale;
	
	ItemStack renderItem;
	public boolean fullRenderItem=false;
	
	private static final RenderVehicle vehicleRenderer;
	static
	{
		vehicleRenderer = new RenderVehicle(Minecraft.getMinecraft().getRenderManager());
	}
	
	public Renderer(int width,int height, EnumRenderType renderType, ItemStack s, float scale) {
		this.renderType=renderType;
		renderItem=s;
		this.scale=scale;
		this.style.width=width;
		this.style.height=height;
	}
	
	@Override
	public void draw(int mouseX, int mouseY) {
		if(style.drawBackground){
			drawRect(this.x, this.y+style.height, this.x+style.width, this.y, style.getBackgroundcolor());
		}
		if(renderType==EnumRenderType.ITEM){
			GL11.glPushMatrix();
		
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glTranslatef(this.x, this.y, -1);

			GL11.glScaled(this.scale, this.scale, this.scale);

			Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(renderItem, 0,0);
			if(fullRenderItem)Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,renderItem, 0,0,null);
			
			RenderHelper.disableStandardItemLighting();
			GL11.glPopMatrix();
			GL11.glPushMatrix();

			if(fullRenderItem&&this.isMouseOnWidget(mouseX, mouseY)) {
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glTranslatef(this.x, this.y, 1);

				
		
			       this.setTooltip(this.getItemToolTip(renderItem));
					GL11.glPopMatrix();
					GL11.glEnable(GL11.GL_SCISSOR_TEST);

			}
			GL11.glPopMatrix();

		}else if(renderType==EnumRenderType.VEHICLE){
			GL11.glPushMatrix();

			if(!(renderItem.getItem() instanceof ItemVehicle))return;
			ItemVehicle itemv = (ItemVehicle)renderItem.getItem();
			VehicleType type = itemv.type;
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(this.x+this.style.margin_left, this.y+style.height/2+this.style.margin_top, 100);
			GL11.glScalef(-110F / type.cameraDistance, 110F / type.cameraDistance, 110F / type.cameraDistance);
			GL11.glScaled(this.scale, this.scale, this.scale);
			GL11.glRotatef(180F, 0F, 0F, 1F);
			GL11.glRotatef(10F, 1F, 0F, 0F);
			GL11.glRotatef(-40, 0F, 1F, 0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
			Utils.drawVehicle(type);
			
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();

		}else if(renderType==EnumRenderType.PLAYER){
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	        drawEntityOnScreen(x, (int) (y+(30+scale)) , (int) (30+scale), (float)(x) - mouseX, (float)(y ) - mouseY, this.mc.player);

		}
		super.draw(mouseX, mouseY);
	}


	  public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
	    {
	        GlStateManager.enableColorMaterial();
	        GlStateManager.pushMatrix();
	        GlStateManager.translate((float)posX, (float)posY, 100.0F);
	        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
	        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
	        float f = ent.renderYawOffset;
	        float f1 = ent.rotationYaw;
	        float f2 = ent.rotationPitch;
	        float f3 = ent.prevRotationYawHead;
	        float f4 = ent.rotationYawHead;
	        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
	        RenderHelper.enableStandardItemLighting();
	        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
	        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
	        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
	        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
	        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
	        ent.rotationYawHead = ent.rotationYaw;
	        ent.prevRotationYawHead = ent.rotationYaw;
	        GlStateManager.translate(0.0F, 0.0F, 0.0F);
	        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
	        rendermanager.setPlayerViewY(180.0F);
	        rendermanager.setRenderShadow(false);
	        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
	        rendermanager.setRenderShadow(true);
	        ent.renderYawOffset = f;
	        ent.rotationYaw = f1;
	        ent.rotationPitch = f2;
	        ent.prevRotationYawHead = f3;
	        ent.rotationYawHead = f4;
	        GlStateManager.popMatrix();
	        RenderHelper.disableStandardItemLighting();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	    }
	
}

