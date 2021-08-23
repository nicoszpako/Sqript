package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypeFile;

import java.io.File;

@Event(
        feature = @Feature(name = "Script load",
                description = "Called when script is loaded.",
                examples = "on script load:",
                pattern = "[script] load[ed]"),
        accessors = {
                @Feature(name = "Script file", description = "The file of this script.", pattern = "[script] file", type = "file"),
        }
)
public class EvtOnScriptLoad extends ScriptEvent {

    public EvtOnScriptLoad(File scriptFile) {
        super(new ScriptTypeAccessor(new TypeFile(scriptFile),"[script] file"));
    }

}
