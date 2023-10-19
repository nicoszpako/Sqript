package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeColor;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Action(name = "Draw Actions",
        features = {
            @Feature(name = "Draw text",description = "Draws a string at a specific position on the screen.",examples = "draw text \"Health : %player's health%\" at [10,10]", pattern = "draw [(1;shadowed)] [(3;centered)] text {string} at {array} [with scale {number}] [[and] with color {number} [(2;without alpha)]]", side = Side.CLIENT),
            @Feature(name = "Draw rectangle",description = "Draws a coloured filled custom sized rectangle at a specific position on the screen.", examples = "draw rectangle at [10,10] with size [20,5] with color 0xFFFF0000",pattern = "draw [colored] rect[angle] at {array} with size {array} [and] with color {number} [(1;without alpha)]", side = Side.CLIENT),
            @Feature(name = "Draw textured rectangle",description = "Draws a textured custom sized rectangle at a specific position on the screen.", examples = "draw textured rectangle at [-15,-7.5] with size [30,15] using texture sample:logo.png",pattern = "draw textured rect[angle] at {array} with size {array} (with|using) texture {resource} [with uv {array}]", side = Side.CLIENT),
            @Feature(name = "Draw line",description = "Draws a line between given positions on the screen.", examples = "draw line from [10,10] to [100,100] with stroke 6 and with color 0",pattern = "draw line from {location} to {location} with stroke {number} [and] with color {number} [(1;without alpha)]", side = Side.CLIENT),
            @Feature(name = "Rotate canvas",description = "Rotate the draw canvas, around a vector if the given vector is not null, else around the Z axis.",
                    examples =
                    "draw text \"Text 1\" at [10,10] #Won't be rotated\n" +
                    "push canvas matrix #Pushes a new matrix onto the matrix pile\n" +
                    "rotate canvas by 90\n" +
                    "draw text \"Text 2\" at [20,20] #Will be displayed rotated by 90 degrees\n" +
                    "pop canvas matrix\n" +
                    "draw text \"Text 3\" at [20,20] #Won't be rotated because the matrix has been popped.",
                    pattern = "rotate canvas by {number} [around {array}] [in ((1;degrees)|(2;radians))]",
                    side = Side.CLIENT),
            @Feature(name = "Translate canvas",description = "Translate the draw canvas.",
                    examples =
                            "draw text \"Text 1\" at [10,10] #Won't be translated\n" +
                                    "push canvas matrix #Pushes a new matrix onto the matrix pile\n" +
                                    "translate canvas by [10,0,0]\n" +
                                    "draw text \"Text 2\" at [20,20] #Will be displayed translated by 10 units to the right\n" +
                                    "pop canvas matrix\n" +
                                    "draw text \"Text 3\" at [20,20] #Won't be translated because the matrix has been popped.",
                    pattern = "translate canvas (by|at) {array}",
                    side = Side.CLIENT),
            @Feature(name = "Scale canvas",description = "Scale the draw canvas.",
                    examples =
                            "draw text \"Text 1\" at [10,10] #Won't be scaled\n" +
                                    "push canvas matrix #Pushes a new matrix onto the matrix pile\n" +
                                    "scale canvas by [2,2,2]\n" +
                                    "draw text \"Text 2\" at [20,20] #Will be displayed scaled by 2\n" +
                                    "pop canvas matrix\n" +
                                    "draw text \"Text 3\" at [20,20] #Won't be scaled because the matrix has been popped.",
                    pattern = "scale canvas by {array}",
                    side = Side.CLIENT),
            @Feature(name = "Push canvas matrix",description = "Pushes a new matrix onto the matrix pile. Allows to \"save the current\" matrix configuration.", examples = "push canvas matrix",pattern = "push canvas matrix", side = Side.CLIENT),
            @Feature(name = "Pop canvas matrix",description = "Pops the top matrix from the matrix pile. Allows to \"come back to the previous\" matrix configuration.", examples = "pop canvas matrix",pattern = "pop canvas matrix", side = Side.CLIENT),
            @Feature(name = "Draw circle",description = "Draws a circle of given radius at given position on the screen.", examples = "draw circle at [10,10] with radius 6 and with color 0",pattern = "draw circle at {location} with radius {number} [and] with color {number} [(1;without alpha)]", side = Side.CLIENT),
            @Feature(name = "Render model",description = "Renders a .json model in the world.", examples = "render model test:my_model at [50,15,20]",pattern = "render model {resource} at {array}", side = Side.CLIENT),
        }
)
public class ActDraw extends ScriptAction {

    @Override
    public void setLine(ScriptToken line) {
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
                if(getParameter(1).get(context) instanceof TypeArray)
                    list = (ArrayList<String>) ((ArrayList)getParameter(1).get(context).getObject()).stream().map(a->((ScriptType)(a)).getObject().toString()).collect(Collectors.toList());
                else {
                    list.add(getParameters().get(0).get(context).toString());
                }
                TypeArray array = (TypeArray) getParameter(2).get(context);
                float scale = getParameterOrDefault(getParameter(3),1d,context).floatValue();
                int color = ScriptManager.parseOrDefault((ScriptType<?>)getParameter(4).get(context),TypeColor.class,new TypeColor(Color.white)).getObject().getRGB();
                if(getMarkValue(2))
                    color = 0xFF000000 | color;

                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GlStateManager.enableBlend();
                GL11.glTranslatef((float)SqriptUtils.getX(array),(float)SqriptUtils.getY(array),(float)SqriptUtils.getZ(array));
                GL11.glScalef(scale,scale,1);
                GL11.glDisable(GL11.GL_CULL_FACE);
                for (int i = 0; i < list.size(); i++) {
                    float textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(list.get(i).replaceAll("&","\247"));
                    Minecraft.getMinecraft().fontRenderer.drawString(list.get(i).replaceAll("&","\247"),getMarkValue(3) ? -scale*textWidth/2 : 0,Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT*i,color,((getMarks()>>1)&1)==1);
                }
                GL11.glEnable(GL11.GL_CULL_FACE);

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
                color = ScriptManager.parseOrDefault((ScriptType<?>)getParameter(3).get(context),TypeColor.class,new TypeColor(Color.white)).getObject().getRGB();
                if(getMarkValue(1))
                    color = 0xFF000000 | color;
                //System.out.println(Integer.toHexString(color));
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GlStateManager.enableBlend();
                float px = (float) location.getVector().x;
                float py = (float) location.getVector().y;
                float pz = (float) location.getVector().z;
                float w = (float) size.getVector().x;
                float h = (float) size.getVector().y;
                GL11.glTranslatef(px,py, pz);
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
                resourceLocation = new ResourceLocation(resourceLocation.getNamespace(),"textures/"+resourceLocation+".png");
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
                color = ScriptManager.parseOrDefault((ScriptType<?>)getParameter(4).get(context),TypeColor.class,new TypeColor(Color.white)).getObject().getRGB();
                if(getMarkValue(1))
                    color = 0xFF000000 | color;
                float red = (float)(color >> 16 & 255) / 255.0F;
                float blue = (float)(color >> 8 & 255) / 255.0F;
                float green = (float)(color & 255) / 255.0F;
                float alpha = 255;
                GL11.glPushMatrix();
                drawLine((float)SqriptUtils.getX(p1),(float)SqriptUtils.getY(p1),(float)SqriptUtils.getZ(p1),SqriptUtils.getX(p2),SqriptUtils.getY(p2),SqriptUtils.getZ(p2),scale,red,green,blue,alpha);
                GL11.glPopMatrix();
                break;
            case 4:
                double angle = getParameterOrDefault(getParameter(1),0d,context);
                //System.out.println(Integer.toBinaryString(getMarks()));
                Vec3d vec3d = ((TypeArray) getParameter(2).get(context)).getVector();
                if(getMarkValue(2))
                    angle = Math.toDegrees(angle);
                GL11.glRotated(angle,vec3d.x,vec3d.y,vec3d.z);
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
            case 9:
                location = (ILocatable) getParameter(1).get(context);
                double radius = (Double) getParameter(2).get(context).getObject();
                color = ScriptManager.parseOrDefault((ScriptType<?>)getParameter(3).get(context),TypeColor.class,new TypeColor(Color.white)).getObject().getRGB();
                if(getMarkValue(1))
                    color = 0xFF000000 | color;
                red = (float)(color >> 16 & 255) / 255.0F;
                blue = (float)(color >> 8 & 255) / 255.0F;
                green = (float)(color & 255) / 255.0F;
                alpha = 1;
                GL11.glPushMatrix();
                drawCircle(location.getVector().x,location.getVector().y,radius,red,green,blue,alpha);
                GL11.glColor3f(1,1,1);
                GL11.glPopMatrix();
                break;
            case 10:
                resourceLocation = (ResourceLocation)getParameter(1).get(context).getObject();
                location = (ILocatable) getParameter(2).get(context);
                GL11.glPushMatrix();
                GL11.glScaled(5,5,5);
                GL11.glTranslated(location.getVector().x,location.getVector().y,location.getVector().z);
                ModelManager modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();

                IBakedModel bakedModel = modelManager.getModel(new ModelResourceLocation(resourceLocation.toString()));
                if(bakedModel == modelManager.getMissingModel()){
                    bakedModel = modelManager.getModel(new ModelResourceLocation(resourceLocation.toString(),"inventory"));
                }
                List<BakedQuad> quads = bakedModel.getQuads(null, null, 0L);
                //System.out.println(modelManager.getMissingModel() == bakedModel);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
                Minecraft.getMinecraft().getRenderItem().renderQuads(bufferbuilder, quads, -1, ItemStack.EMPTY);
                tessellator.draw();

                GL11.glPopMatrix();
                //System.out.println("Rendering");
                break;
        }

    }

    public static void drawCircle(double x, double y, double r, float re, float g, float b, float a) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(x, y,0).endVertex();
        // for some the circle is only drawn if theta is decreasing rather than
        // ascending
        GlStateManager.color(re,g,b,a);

        double end = Math.PI * 2.0;
        double incr = end / 40d;
        for (double theta = -incr; theta < end; theta += incr) {
            vertexbuffer.pos(x + r * Math.cos(-theta), y + r * Math.sin(-theta), 0).endVertex();
        }
        // renderer.finishDrawing();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawLine(double x,double y, double z,double x2, double y2, double z2,float lineWidth, float r, float g, float b, float a) {
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
        vertexbuffer.pos(x2, y2, z2).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glColor3f(1,1,1);

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
