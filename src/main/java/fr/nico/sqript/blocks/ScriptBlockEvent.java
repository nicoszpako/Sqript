package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.meta.EventDefinition;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;

import java.util.Arrays;

@Block(name = "event",
        description = "Event blocks",
        examples = "on player death:",
        regex = "^on .*",
        fields = {"side"}
)
public class ScriptBlockEvent extends ScriptBlock {

    //ScriptWrapper déclenchant un IScript muni du contexte donné par un objet de type "eventType", wrappé lors de l'appel de l'event (voir ScriptManager.callEvent()).

    public Class<? extends ScriptEvent> eventType;
    public ScriptType[] parameters;
    public int marks;

    //By default, events will launch on server
    public Side side;

    public ScriptBlockEvent(ScriptLine head) throws ScriptException {
        super(head);
        getHead().text = head.text.trim().replaceFirst("(^|\\s+)on\\s+", ""); //Extracting the event parameters
        getHead().text = head.text.substring(0,head.text.length()-1); //Removing the last ":"
    }

    @Override
    public void init(ScriptLineBlock block) throws Exception {
        this.eventType = getEvent(getHead());
        if(eventType == null)
            throw new ScriptException.ScriptUnknownEventException(getHead());
        this.side = eventType.getAnnotation(Event.class).side();
        super.init(block);
    }

    public Class<? extends ScriptEvent> getEvent(ScriptLine line) {
        for (EventDefinition eventDefinition : ScriptManager.events) {
            //System.out.println("Checking for : "+line+" with "+eventDefinition.eventClass);
            int matchedPatternIndex = -1;
            if ((matchedPatternIndex = eventDefinition.getMatchedPatternIndex(line.text)) != -1) {
                //System.out.println(matchedPatternIndex);
                //Parsing the arguments
                String[] arguments = eventDefinition.getTransformedPatterns()[matchedPatternIndex].getAllArguments(line.text);
                //System.out.println(eventDefinition.eventClass.getSimpleName()+" "+arguments.length);
                parameters = new ScriptType[arguments.length];
                marks = eventDefinition.getTransformedPatterns()[matchedPatternIndex].getAllMarks(line.text);
                for (int i = 0; i < arguments.length; i++) {
                    try {
                        parameters[i] = ScriptDecoder.getExpression(line.with(arguments[i]),new ScriptCompileGroup()).get(ScriptContext.fromGlobal());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return eventDefinition.getEventClass();
            }
        }
        return null;
    }

    @Override
    protected void load() throws Exception {
        if(fieldDefined("side"))
            side = Side.from(getSubBlock("side").getRawContent());

        if(side!=null && !side.isStrictlyValid())
            return;

        ScriptCompileGroup group = new ScriptCompileGroup();
        group.addArray(Arrays.asList(eventType.getAnnotation(Event.class).accessors()));
        setRoot(getMainField().compile(group));
        getScriptInstance().registerBlock(this);

    }

    public ScriptType[] getParameters() {
        return parameters;
    }

    public Class<? extends ScriptEvent> getEventType() {
        return eventType;
    }

    public int getMarks() {
        return marks;
    }
}