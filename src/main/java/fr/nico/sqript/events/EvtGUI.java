package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;
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
                    @Feature(name="GUI class name", description = "The JAVA class name of the GUI being opened", pattern = "(GUI|gui) [class] name", type="string")
            }
    )
    public static class EvtGUIOpen extends ScriptEvent {

        public EvtGUIOpen(GuiScreen guiScreen) {
            super(new ScriptTypeAccessor(new TypeString(guiScreen == null ? "undefined" : guiScreen.getClass().getSimpleName()),"(GUI|gui) [class] name"));
        }
    }

    @Event(
            feature = @Feature(name = "Button click",
                    description = "Called when a button is clicked.",
                    examples = "on button click:",
                    pattern = "(button click|click on [a] button)",
                    side = Side.CLIENT),
            accessors = {
                    @Feature(name="Clicked button id", description = "The clicked button's id.", pattern = "button id", type = "number")
            }
    )
    public static class EvtButtonClicked extends ScriptEvent {

        public EvtButtonClicked(int buttonId) {
            super(new ScriptTypeAccessor(new TypeNumber(buttonId),"button id"));
        }
    }

}
