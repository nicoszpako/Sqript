package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import javafx.scene.paint.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Action(name = "Draw Actions",
        description ="Draw something on the screen. Must be used in an appropriated event !",
        examples = {"draw \"Hello !\" at [0,100] with scale 2"
        },
        patterns = {
            "draw [(1;shadowed)] text {string} at {array} [with scale {number}] [[and] with color {number}]",
            "draw [colored] rect[angle] at {array} with size {array} [and] with color {number} [(1;without alpha)]",
            "draw textured rect[angle] at {array} with size {array} (with|using) texture {resource} [with uv {array}]",
            "draw line from {array} to {array} with stroke {number} [and] with color {number}",
            "rotate canvas by {number} [((1;degrees)|(2;radians))]", //Default is degrees
            "translate canvas by {array}",
            "scale canvas by {array}",
            "push canvas matrix",
            "pop canvas matrix"
        },
        side = Side.CLIENT
)
public class ActDraw extends ScriptAction {

    @Override
    public void setLine(ScriptLine line) {
        super.setLine(line);
        //System.out.println("Set draw line to : "+line);
        //System.out.println("getLine gives : "+getLine());
    }

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){

            case 0:
                ArrayList<String> list = new ArrayList<>();
                //System.out.println("A:"+getParameter(1).getClass());
                //System.out.println("B:"+getParameter(1).get(context).getClass());
                if(getParameter(1).get(context) instanceof TypeString)
                    list.add((String) getParameter(1,context));
                else if(getParameter(1).get(context) instanceof TypeArray)
                    list = (ArrayList<String>) ((ArrayList)getParameter(1).get(context).getObject()).stream().map(a->((ScriptType)(a)).getObject().toString()).collect(Collectors.toList());
                TypeArray array = (TypeArray) getParameter(2).get(context);
                float scale = getParameterOrDefault(getParameter(3),1d,context).floatValue();
                int color = getParameterOrDefault(getParameter(4),(double)0xFFFFFFFF,context).intValue();
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GlStateManager.enableBlend();
                GL11.glTranslatef((float)SqriptUtils.getX(array),(float)SqriptUtils.getY(array),(float)SqriptUtils.getZ(array));
                GL11.glScalef(scale,scale,1);
                for (int i = 0; i < list.size(); i++) {
                    Minecraft.getMinecraft().fontRenderer.drawString(list.get(i).replaceAll("&","\247"),0,Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT*i,color,((getMarks()>>1)&1)==1);
                }
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.resetColor();
                GL11.glColor3f(1,1,1);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
                break;
            case 1:
                ILocatable location = (ILocatable) getParameter(1).get(context);
                ILocatable size = (ILocatable) getParameter(2).get(context);
                color = getParametersSize() >=3 ? ((Double) getParameter(3,context)).intValue() :0xFFFFFFFF;
                if(getMarkValue(1))
                    color = 0xFF000000 | color;
                //System.out.println(Integer.toHexString(color));
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GlStateManager.enableBlend();
                float px = (float) location.getVector().x;
                float py = (float) location.getVector().y;
                float w = (float) size.getVector().x;
                float h = (float) size.getVector().y;
                GL11.glTranslatef(px,py, 0);
                drawRect(0,0,w,h,color);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.resetColor();
                GL11.glColor3f(1,1,1);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
                break;
            case 2:
                array = (TypeArray) getParameter(1).get(context);
                size = (ILocatable) getParameter(2).get(context);
                ScriptType resourceType = getParameter(3).get(context);
                ResourceLocation resourceLocation = null;
                //System.out.println("1:"+(getLine()==null));
                //System.out.println("2:"+(getLine().scriptInstance==null));
                //System.out.println("3:"+(getLine().scriptInstance.getName()==null));
                resourceLocation = (ResourceLocation) resourceType.getObject();
                resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(),"textures/"+resourceLocation.getResourcePath()+".png");
                TypeArray uv = getParameter(4) != null ? (TypeArray) getParameter(4).get(context) : null;
                double u1 = uv != null && uv.getObject().size()>0 ? ((Double)uv.getObject().get(0).getObject()): 0.0D;
                double v1 = uv != null && uv.getObject().size()>1 ? ((Double)uv.getObject().get(1).getObject()): 0.0D;
                double u2 = uv != null && uv.getObject().size()>2 ? ((Double)uv.getObject().get(2).getObject()): 1.0D;
                double v2 = uv != null && uv.getObject().size()>3 ? ((Double)uv.getObject().get(3).getObject()): 1.0D;
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glTranslatef((float)SqriptUtils.getX(array),(float)SqriptUtils.getY(array),(float)SqriptUtils.getZ(array));
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                drawTexturedRect(0, 0, 0, size.getVector().x,size.getVector().y, u1,v1,u2,v2);
                GL11.glPopAttrib();
                GL11.glColor3f(1,1,1);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                break;
            case 3:
                TypeArray p1 = (TypeArray) getParameter(1).get(context);
                TypeArray p2 = (TypeArray) getParameter(2).get(context);
                scale = getParametersSize()>=3? ((Double) getParameter(3,context)).floatValue() :1;
                color = getParametersSize()>=4? ((Double) getParameter(4,context)).intValue() :0xFFFFFF;
                float red = (float)(color >> 16 & 255) / 255.0F;
                float blue = (float)(color >> 8 & 255) / 255.0F;
                float green = (float)(color & 255) / 255.0F;
                float alpha = 255;
                GL11.glPushMatrix();
                drawLine((float)SqriptUtils.getX(p1),(float)SqriptUtils.getY(p1),(float)SqriptUtils.getZ(p1),SqriptUtils.getX(p2),SqriptUtils.getY(p2),scale,red,green,blue,alpha);
                GL11.glColor3f(1,1,1);

                GL11.glPopMatrix();
                break;
            case 4:
                double angle = getParameterOrDefault(getParameter(1),0d,context);
                //System.out.println(Integer.toBinaryString(getMarks()));
                if(getMarkValue(2))
                    angle = Math.toDegrees(angle);
                GL11.glRotated(angle,0,0,1);
                break;
            case 5:
                ILocatable locatable = (ILocatable) getParameter(1).get(context);
                Vec3d v = locatable.getVector();
                GL11.glTranslated(v.x,v.y,v.z);
                break;
            case 6:
                ScriptType type =  getParameter(1).get(context);
                double x = 1d;
                double y = 1d;
                double z = 1d;
                if(type instanceof TypeNumber){
                    double value = ((TypeNumber)type).getObject();
                    x = value;
                    y = value;
                    z = 1;
                }else if (type instanceof  ILocatable){
                    locatable = (ILocatable) type;
                    v = locatable.getVector();
                    x = v.x;
                    y = v.y;
                    z = v.z;
                }
                GL11.glScaled(x,y,z);
                break;
            case 7:
                GL11.glPushMatrix();
                break;
            case 8:
                GL11.glPopMatrix();
                break;
        }

    }
    public static void drawLine(double x,double y, double z,double x2, double y2, float lineWidth, float r, float g, float b, float a) {
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth);
        GlStateManager.color(r,g,b,a);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x, y, z).endVertex();
        vertexbuffer.pos(x2, y2, z).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

    }

    public void drawRect(float left, float top, float right, float bottom, int color)
    {
        //System.out.println(getLine()+" Drawing at "+left+" "+top+" "+" with color : "+color);
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(left,bottom);
        GL11.glVertex2d(right,bottom);
        GL11.glVertex2d(right,top);
        GL11.glVertex2d(left,top);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }



    public static void drawTexturedRect(double x, double y, double w, double h) {
        drawTexturedRect(x, y, 0, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
    }

    public static void drawTexturedRect(double x, double y, double z, double w, double h, double u1, double v1, double u2,
                                        double v2) {
            GlStateManager.pushMatrix();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x + w, y, z).tex(u2, v1).endVertex();
            vertexbuffer.pos(x, y, z).tex(u1, v1).endVertex();
            vertexbuffer.pos(x, y + h, z).tex(u1, v2).endVertex();
            vertexbuffer.pos(x + w, y + h, z).tex(u2, v2).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
    }

}
