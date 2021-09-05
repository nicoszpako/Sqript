package fr.nico.sqript.meta;

import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;

public class EventDefinition {

    public Class<? extends ScriptEvent> eventClass;
    private TransformedPattern[] transformedPatterns;
    private Feature feature;
    private Feature[] accessors;

    public EventDefinition(Class<? extends ScriptEvent> eventClass, Feature feature, Feature[] accessors) throws Exception {
        this.feature = feature;
        this.accessors = accessors;
        this.eventClass = eventClass;
        if(feature!=null){
            try {
                this.transformedPatterns = new TransformedPattern[]{ScriptDecoder.transformPattern(feature.pattern())};
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

    }

    public Feature getFeature() {
        return feature;
    }

    public Feature[] getAccessors() {
        return accessors;
    }

    public EventDefinition setAccessors(Feature... accessors){
        this.accessors = accessors;
        this.transformedPatterns = new TransformedPattern[accessors.length];
        for(int i = 0; i<this.accessors.length; i++){
            try {
                this.transformedPatterns[i]= ScriptDecoder.transformPattern(accessors[i].pattern());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public TransformedPattern[] getTransformedPatterns() {
        return transformedPatterns;
    }

    public int getMatchedPatternIndex(String line){
        for (int i = 0; i < transformedPatterns.length ; i++) {
            //System.out.println("Checking if "+line+" matches "+transformedPatterns[i].getPattern().pattern());
            if(transformedPatterns[i].getPattern().matcher(line).matches())
                return i;
        }
        return -1;
    }

    public Class<? extends ScriptEvent> getEventClass() {
        return eventClass;
    }



}
