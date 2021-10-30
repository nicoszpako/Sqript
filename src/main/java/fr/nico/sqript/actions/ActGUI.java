package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.relauncher.SideOnly;

@Action(name = "GUI Actions",
        features = {@Feature(name = "Close GUI", description = "Close the current GUI.", examples = "close current gui", pattern = "close [the] [current] (GUI|gui)", side = Side.CLIENT),
                    @Feature(name = "Open settings GUI", description = "Opens the settings GUI.", examples = "open settings", pattern = "open [the] settings [(GUI|gui)]", side = Side.CLIENT),
                    @Feature(name = "Open single-player GUI", description = "Opens the world selection GUI.", examples = "open single-player gui", pattern = "open [the] (single-player|world selection) [(GUI|gui)]", side = Side.CLIENT),
                    @Feature(name = "Open multi-player GUI", description = "Opens the server selection GUI.", examples = "open multi-player gui", pattern = "open [the] (multi-player|server selection) [(GUI|gui)]", side = Side.CLIENT),
        }
)
public class ActGUI extends ScriptAction {

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft().gameSettings));
                break;
            case 2:
                Minecraft.getMinecraft().displayGuiScreen(new GuiWorldSelection(Minecraft.getMinecraft().currentScreen));
                break;
            case 3:
                Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayer(Minecraft.getMinecraft().currentScreen));
                break;
        }
    }
}
