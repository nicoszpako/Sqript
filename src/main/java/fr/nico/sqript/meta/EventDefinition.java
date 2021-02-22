package fr.nico.sqript.meta;

import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;

public class EventDefinition {

    final String[] example;
    final String name;
    final String[] description;
    final String[] patterns;
    final Side side;
    String[] accessors;
    public TransformedPattern[] transformedPatterns;
    public TransformedPattern[] transformedAccessors;

    public EventDefinition setAccessors(String... data){
        accessors = data;
        this.transformedAccessors = new TransformedPattern[accessors.length];
        for(int i = 0; i<this.accessors.length; i++){
            try {
                String[] t = ScriptDecoder.splitAtDoubleDot(accessors[i]);
                if(t.length!=2)
                    throw new Exception(accessors[i]+" accessor format is not correct ! It must be <pattern>:<typeReturned>");
                this.transformedAccessors[i]= ScriptDecoder.transformPattern(t[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public int getMatchedPatternIndex(String line){
        for (int i = 0; i < transformedPatterns.length ; i++) {
            //System.out.println("Checking if "+line+" matches "+transformedPatterns[i].regex);
            if(transformedPatterns[i].getPattern().matcher(line).matches())return i;
        }
        return -1;
    }

    public Class<? extends ScriptEvent> cls;

    public Class<? extends ScriptEvent> getEventClass() {
        return cls;
    }

    public EventDefinition(String name, String[] description, String[] example, Class<? extends ScriptEvent> cls, Side side, @Nullable String... patterns) {
        this.name = name;
        this.example = example;
        this.description = description;
        this.patterns = patterns;
        this.side = side;
        this.cls=cls;
        if(patterns!=null){
            this.transformedPatterns = new TransformedPattern[patterns.length];
            for(int i = 0;i<this.patterns.length;i++){
                try {
                    String[] t = this.patterns[i].split(":",2);
                    this.transformedPatterns[i]=ScriptDecoder.transformPattern(t[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getName() {
        return name;
    }

    public String[] getExample() {
        return example;
    }

    public String[] getDescription() {
        return description;
    }

    public String[] getPatterns() {
        return patterns;
    }



}
