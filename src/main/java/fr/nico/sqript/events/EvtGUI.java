package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.core.script.Script;


public class EvtGUI {

    @Event(
            feature = @Feature(name = "GUI opened",
                    description = "Called when a GUI is being opened.",
                    examples = "on gui open:",
                    pattern = "(GUI|gui) open[ed]",
                    side = Side.CLIENT),
            accessors = {
                    @Feature(name="GUI class name", description = "The JAVA class name of the GUI being opened", pattern = "(GUI|gui) [class] name")
            }
    )
    public static class EvtGUIOpen extends ScriptEvent {

        public EvtGUIOpen(GuiScreen guiScreen) {
            super(new ScriptTypeAccessor(new TypeString(guiScreen == null ? "undefined" : guiScreen.getClass().getSimpleName()),"(GUI|gui) [class] name"));
        }
    }


}
