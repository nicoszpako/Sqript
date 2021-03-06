package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Gui Expressions",
        description = "Gather information about GUIs",
        examples = "screen width",
        patterns = {
            "screen width:number",
            "screen height:number",
            "screen width of {string}:number",
            "screen height of font:number"
        },
        side = Side.CLIENT
)
public class ExprGui extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        switch(getMatchedIndex()){
            case 0:
                return new TypeNumber(resolution.getScaledWidth_double()*resolution.getScaleFactor());
            case 1:
                return new TypeNumber(resolution.getScaledHeight_double()*resolution.getScaleFactor());
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
