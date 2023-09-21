package fr.nico.sqript.expressions;

import com.google.common.collect.Lists;
import fr.nico.sqript.HSLColor;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Expression(name = "Render Expressions",
        features = {
                @Feature(name = "Player's skin", description = "Returns the player's skin as an image.", examples = "player's skin", pattern = "{player}['s] skin", type = "image"),
                @Feature(name = "Image width", description = "Returns the given image's width.", examples = "player's skin width", pattern = "{image}['s] width", type = "number"),
                @Feature(name = "Image height", description = "Returns the given image's height.", examples = "player's skin height", pattern = "{image}['s] height", type = "number"),
                @Feature(name = "Red color", description = "Returns the full red opaque color.", examples = "red", pattern = "red", type = "color",settable = false),
                @Feature(name = "Green color", description = "Returns the full green opaque color.", examples = "green", pattern = "green", type = "color",settable = false),
                @Feature(name = "Blue color", description = "Returns the full blue opaque color.", examples = "blue", pattern = "blue", type = "color",settable = false),
                @Feature(name = "White color", description = "Returns the full white opaque color.", examples = "white", pattern = "white", type = "color",settable = false),
                @Feature(name = "Black color", description = "Returns the full black opaque color.", examples = "black", pattern = "black", type = "color",settable = false),
                @Feature(name = "Gray color", description = "Returns the full gray opaque color.", examples = "gray", pattern = "gray", type = "color",settable = false),
                @Feature(name = "Lighter color", description = "Returns the given color lightened by 20%.", examples = "light red", pattern = "light[er] {color}", type = "color",settable = false),
                @Feature(name = "Darker color", description = "Returns the given color darkened by 20%.", examples = "dark red", pattern = "dark[er] {color}", type = "color",settable = false),
                @Feature(name = "Red component of color", description = "Returns the given color's red component.", examples = "dark red's red component", pattern = "{color}['s] red component", type = "number"),
                @Feature(name = "Green component of color", description = "Returns the given color's green component.", examples = "dark red's green component", pattern = "{color}['s] green component", type = "number"),
                @Feature(name = "Blue component of color", description = "Returns the given color's blue component.", examples = "dark red's blue component", pattern = "{color}['s] blue component", type = "number"),
                @Feature(name = "Alpha component of color", description = "Returns the given color's alpha component.", examples = "dark red's alpha component", pattern = "{color}['s] alpha component", type = "number"),
                @Feature(name = "Brightness of color", description = "Returns the given color's brightness as a number between 0 and 1.", examples = "dark red's brightness", pattern = "{color}['s] brightness", type = "number"),
                @Feature(name = "Color with components", description = "Returns the color having the given [r,g,b,a] components. If the array has only 3 elements, then the color will be opaque.", examples = "color with components [255,0,0]", pattern = "color with components {array}", type = "color"),
        }
)
public class ExprRender extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                EntityPlayer player = ((TypePlayer)parameters[0]).getObject();
                try {
                    return new TypeImage(ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID()).getLocationSkin()).getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case 1:
                TypeImage image = ScriptManager.parse(parameters[0],TypeImage.class);
                return new TypeNumber(image.getObject().getWidth());
            case 2:
                image = ScriptManager.parse(parameters[0],TypeImage.class);
                return new TypeNumber(image.getObject().getHeight());
            case 3:
                return new TypeColor(Color.RED);
            case 4:
                return new TypeColor(Color.GREEN);
            case 5:
                return new TypeColor(Color.BLUE);
            case 6:
                return new TypeColor(Color.WHITE);
            case 7:
                return new TypeColor(Color.BLACK);
            case 8:
                return new TypeColor(Color.GRAY);
            case 9:
                TypeColor typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                Color color = typeColor.getObject();
                HSLColor hslColor = new HSLColor(color);
                float luminance = hslColor.getLuminance();
                color = hslColor.adjustLuminance(Math.min(luminance+15,100));
                return new TypeColor(color);
            case 10:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                color = typeColor.getObject();
                hslColor = new HSLColor(color);
                luminance = hslColor.getLuminance();
                color = hslColor.adjustLuminance(Math.max(luminance-15,100));
                return new TypeColor(color);
            case 11:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                return new TypeNumber(typeColor.getObject().getRed());
            case 12:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                return new TypeNumber(typeColor.getObject().getGreen());
            case 13:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                return new TypeNumber(typeColor.getObject().getBlue());
            case 14:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                return new TypeNumber(typeColor.getObject().getAlpha());
            case 15:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                double s = (typeColor.getObject().getRed()/255f + typeColor.getObject().getGreen()/255f + typeColor.getObject().getBlue()/255f)/3f;
                return new TypeNumber(s);
            case 16:
                ArrayList<ScriptType<?>> components = ((TypeArray)parameters[0]).getObject();
                int red = components.size() > 0 ? ((Double) components.get(0).getObject()).intValue() : 255;
                int green = components.size() > 1 ? ((Double) components.get(1).getObject()).intValue() : 255;
                int blue = components.size() > 2 ? ((Double) components.get(2).getObject()).intValue() : 255;
                int alpha = components.size() > 3 ? ((Double) components.get(3).getObject()).intValue() : 255;
                return new TypeColor(new Color(red,green,blue,alpha));
        }

        return null;
    }

    public int brighten(int value, float factor){
        return (int) Math.min(value*factor,255);
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                EntityPlayer player = ((TypePlayer)parameters[0]).getObject();
                if(SqriptForge.getClientProxy().getCustomPlayerBufferedSkins().containsKey(player.getName()))
                    SqriptForge.getClientProxy().getCustomPlayerSkins().remove(player.getName());
                SqriptForge.getClientProxy().getCustomPlayerBufferedSkins().put(player.getName(),((TypeImage)to).getObject());
                return true;
            case 11:
                TypeColor typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                Color color = typeColor.getObject();
                int value = ((TypeNumber)to).getObject().intValue();
                typeColor.setObject(new Color(value,color.getGreen(),color.getBlue(),color.getAlpha()));
            case 12:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                color = typeColor.getObject();
                value = ((TypeNumber)to).getObject().intValue();
                typeColor.setObject(new Color(color.getRed(),value,color.getBlue(),color.getAlpha()));
            case 13:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                color = typeColor.getObject();
                value = ((TypeNumber)to).getObject().intValue();
                typeColor.setObject(new Color(color.getRed(),color.getGreen(),value,color.getAlpha()));
            case 14:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                color = typeColor.getObject();
                value = ((TypeNumber)to).getObject().intValue();
                typeColor.setObject(new Color(color.getRed(),color.getGreen(),color.getBlue(),value));
            case 15:
                typeColor = ScriptManager.parse(parameters[0],TypeColor.class);
                color = typeColor.getObject();
                double b1 = (typeColor.getObject().getRed()/255f + typeColor.getObject().getGreen()/255f + typeColor.getObject().getBlue()/255f)/3f;
                double b2 = ((TypeNumber)to).getObject();
                double f = b2/b1;
                int r = (int) Math.min((int)(color.getRed()*f),255);
                int g = (int) Math.min((int)(color.getGreen()*f),255);
                int b = (int) Math.min((int)(color.getBlue()*f),255);
                int a = (int) Math.min((int)(color.getAlpha()*f),255);
                typeColor.setObject(new Color(r,g,b,a));

        }
        return false;
    }


}
