package fr.nico.sqript.expressions;

import fr.nico.sqript.forge.gui.Frame;
import fr.nico.sqript.forge.gui.Image;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeFrame;
import fr.nico.sqript.types.TypeImage;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

@Expression(name = "Frame Expressions",
        features = {
                @Feature(name = "Frame", description = "Returns a new empty frame.", examples = "a new frame", pattern = "a [new] frame", type = "frame", side = Side.CLIENT),
                @Feature(name = "Frame image", description = "Returns a new image.", examples = "image test:gui/my_image", pattern = "[a[n]] [new] image [of] {resource}", type = "image", side = Side.CLIENT),
                @Feature(name = "Button", description = "Returns a new button.", examples = "button with texture test:gui/button_texture at [10,20] sized [20,40] and with id 0", pattern = "button [with texture {resource}] [at {array}] [sized {array}] [[and] with id {number}]", type = "button", side = Side.CLIENT),
                @Feature(name = "Label", description = "Returns a new label.", examples = "label displaying \"hello !\" with scale 5 and with color 0xFF0000", pattern = "button [with texture {resource}] [at {array}] [sized {array}] [[and] with id {number}]", type = "button", side = Side.CLIENT),
        }
)
public class ExprFrame extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        switch (getMatchedIndex()) {
            case 0:
                return new TypeFrame(new Frame());
            case 1:
                ResourceLocation resourceLocation = (ResourceLocation) parameters[0].getObject();

                return new TypeImage(new Image(new ResourceLocation(resourceLocation.toString()),100,100));
            case 2:
                String text = (String) parameters[0].getObject();
                return new TypeNumber(Minecraft.getMinecraft().fontRenderer.getStringWidth(text));
            case 3:
                return new TypeNumber(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
