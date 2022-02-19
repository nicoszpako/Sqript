package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@Expression(name = "Gui Expressions",
        features = {
                @Feature(name = "Screen width", description = "Returns the screen width.", examples = "screen width", pattern = "screen width", type = "number", side = Side.CLIENT),
                @Feature(name = "Screen height", description = "Returns the screen height.", examples = "screen height", pattern = "screen height", type = "number", side = Side.CLIENT),
                @Feature(name = "Display width of a string", description = "Returns the display width of a string.", examples = "display width of \"Hello !\"", pattern = "display width of {string}", type = "number", side = Side.CLIENT),
                @Feature(name = "Display height of a string", description = "Returns the display height of a string.", examples = "display height of \"Hello !\"", pattern = "display height of {string}", type = "number", side = Side.CLIENT),
        }
)
public class ExprGui extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        switch (getMatchedIndex()) {
            case 0:
                return new TypeNumber(resolution.getScaledWidth_double());
            case 1:
                return new TypeNumber(resolution.getScaledHeight_double());
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
