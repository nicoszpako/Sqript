package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.forge.gui.Frame;
import fr.nico.sqript.forge.gui.FrameUtils;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraft.client.Minecraft;

@Action(name = "Frame Actions",
        features = @Feature(name = "Open frame", description = "Opens a defined frame.", examples = "open frame {my_frame}", pattern = "open frame {frame}")
)
public class ActFrame extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                Frame frame = (Frame) getParameter(1,context);
                Minecraft.getMinecraft().displayGuiScreen(frame);
                break;
        }
    }
}
