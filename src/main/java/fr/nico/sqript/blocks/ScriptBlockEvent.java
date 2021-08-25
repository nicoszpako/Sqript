package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Event;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.meta.EventDefinition;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;

import java.util.Arrays;

@Block(
        feature = @Feature(name = "Event",
                description = "Run code when a specific event is triggered.",
                examples = "on script load:\n" +
                        "    print \"Hello world !\"",
                regex = "^on .*"),
        fields = {@Feature(name = "side")}
)
public class ScriptBlockEvent extends ScriptBlock {

    public Class<? extends ScriptEvent> eventType;
    public ScriptType[] parameters;
    public int marks;

    //By default, events will launch on server
    public Side side;

    public ScriptBlockEvent(ScriptToken head) throws ScriptException {
        super(head);
        getHead().setText(head.getText().trim().replaceFirst("(^|\\s+)on\\s+", "")); //Extracting the event parameters
        getHead().setText(head.getText().substring(0,head.getText().length()-1)); //Removing the last ":"
    }

    @Override
    public void init(ScriptLineBlock block) throws Exception {
        this.eventType = getEvent(getHead());
        if(eventType == null)
            throw new ScriptException.ScriptUnknownEventException(getHead());
        this.side = eventType.getAnnotation(Event.class).feature().side();
        super.init(block);
    }

    public Class<? extends ScriptEvent> getEvent(ScriptToken line) {
        for (EventDefinition eventDefinition : ScriptManager.events) {
            //System.out.println("Checking for : "+line+" with "+eventDefinition.eventClass);
            int matchedPatternIndex = -1;
            if ((matchedPatternIndex = eventDefinition.getMatchedPatternIndex(line.getText())) != -1) {
                //System.out.println(matchedPatternIndex);
                //Parsing the arguments
                String[] arguments = eventDefinition.getTransformedPatterns()[matchedPatternIndex].getAllArguments(line.getText());
                //System.out.println(eventDefinition.eventClass.getSimpleName()+" "+arguments.length);
                parameters = new ScriptType[arguments.length];
                marks = eventDefinition.getTransformedPatterns()[matchedPatternIndex].getAllMarks(line.getText());
                for (int i = 0; i < arguments.length; i++) {
                    try {
                        parameters[i] = ScriptDecoder.parseExpression(line.with(arguments[i]),new ScriptCompileGroup()).get(ScriptContext.fromGlobal());
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