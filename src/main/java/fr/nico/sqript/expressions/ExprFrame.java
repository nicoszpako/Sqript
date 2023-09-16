package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.forge.gui.*;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

@Expression(name = "Frame Expressions",
        features = {
                @Feature(name = "Frame", description = "Returns a new empty frame.", examples = "a new frame", pattern = "a [new] frame", type = "frame", side = Side.CLIENT),
                @Feature(name = "Image widget", description = "Returns a new image widget.", examples = "image test:gui/my_image.png", pattern = "[a[n]] [new] image [of] {resource}", type = "image widget", side = Side.CLIENT),
                @Feature(name = "Button", description = "Returns a new button.", examples = "button with texture test:gui/button_texture.png displaying \"Hi !\" at [10,20] sized [20,40] and with id 0", pattern = "button [with texture {resource}] [displaying {string}] [at {array}] [sized {array}] [[and] with [button] id {number}]", type = "button", side = Side.CLIENT),
                @Feature(name = "Label", description = "Returns a new label.", examples = "label displaying \"hello !\" at [15,15] with scale 5 and with color 0xFF0000", pattern = "[(1;centered)] label displaying [text] {string} at {array} [with scale {number}] [[and] with color {number}]", type = "label", side = Side.CLIENT),
                @Feature(name = "Label display text", description = "Returns the display text of a label.", examples = "set {label} display test to \"Hi !\"", pattern = "{label}['s] [display] text", type = "string", side = Side.CLIENT),
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

                return new TypeImageWidget(new Image(new ResourceLocation(resourceLocation.toString()),100,100));
            case 2:
                resourceLocation = (ResourceLocation) getParameterOrDefault(parameters[0],null);
                String text = getParameterOrDefault(parameters[1],"");
                Vec3d pos = parameters[2] == null ? Vec3d.ZERO : SqriptUtils.arrayToLocation((ArrayList) parameters[2].getObject());
                Vec3d size = parameters[3] == null ? new Vec3d(100,20,0): SqriptUtils.arrayToLocation((ArrayList) parameters[3].getObject());
                int id = getParameterOrDefault(parameters[4],0d).intValue();
                Button button = new Button((int)pos.x,(int)pos.y,(int)size.x,(int)size.y,text);
                button.setResourceLocation(resourceLocation);
                if (resourceLocation != null) {
                    button.setDrawType(Button.DRAW_TEXT_AND_TEXTURE);
                    button.style.setBackgroundcolor(0);
                    button.style.setHoverColor(0);
                } else {
                    button.setDrawType(Button.DRAW_TEXT);
                }
                button.setButtonId(id);
                return new TypeButton(button);
            case 3:
                text = getParameterOrDefault(parameters[0],"");
                pos = parameters[1] == null ? Vec3d.ZERO : SqriptUtils.arrayToLocation((ArrayList) parameters[1].getObject());
                float scale =  getParameterOrDefault(parameters[2],1d).floatValue();
                int color = getParameterOrDefault(parameters[3],0xFFFFFFFF);
                Label label = new Label(text,color,scale);
                label.setX((int)pos.x);
                label.setY((int)pos.y);
                if(getMarks()>>1==1)
                    label.setStyleDisplay(EnumDisplayStyle.CENTER);
                return new TypeLabel(label);
            case 4:
                label = (Label) parameters[0].getObject();
                return new TypeString(label.getDisplayText());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 4:
                Label label = (Label) parameters[0].getObject();
                String text = (String)to.getObject();
                label.setDisplayText(text);
                return true;
        }
        return false;
    }
}
