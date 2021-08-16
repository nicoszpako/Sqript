package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypeFile;

import java.io.File;

@Event(name = "script load",
        description = "Called when script is loaded",
        examples = "on script load:",
        patterns = "[script] load[ed]",
        accessors = "[script] file:file")
public class EvtOnScriptLoad extends ScriptEvent {

    public EvtOnScriptLoad(File scriptFile) {
        super(new ScriptTypeAccessor(new TypeFile(scriptFile),"[script] file"));
    }

}
