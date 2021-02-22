package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.meta.EventDefinition;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptInstance;

import java.util.Arrays;
import java.util.List;

@Block(name = "event",
        description = "Event blocks",
        examples = "on player death:",
        regex = "^on .*")
public class ScriptBlockEvent extends ScriptBlock {

    //ScriptWrapper déclenchant un IScript muni du contexte donné par un objet de type "eventType", wrappé lors de l'appel de l'event (voir ScriptManager.callEvent()).

    public Class<? extends ScriptEvent> eventType;

    public ScriptBlockEvent(ScriptLine head) throws ScriptException {
        this.eventType = getEvent(head);
        if (eventType == null) {
            throw new ScriptException.ScriptUnknownEventException(head);
        }
    }

    public Class<? extends ScriptEvent> getEvent(ScriptLine line) {
        line.text = line.text.replaceAll("on\\s+", "").replaceAll(":", "");
        for (EventDefinition eventDefinition : ScriptManager.events.values()) {
            if (eventDefinition.getMatchedPatternIndex(line.text) != -1)
                return eventDefinition.getEventClass();
        }
        return null;
    }

    @Override
    public void init(ScriptInstance scriptInstance, ScriptLineBlock scriptLineBlock) throws Exception {
        ScriptCompileGroup group = new ScriptCompileGroup();
        group.addArray(Arrays.asList(eventType.getAnnotation(Event.class).accessors()));
        setRoot(scriptLineBlock.compile(group));
        scriptInstance.registerBlock(this);
    }

}