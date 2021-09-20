package fr.nico.sqript.meta;

import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.regex.Matcher;

public class ActionDefinition {

    String name;
    Feature[] features;
    public TransformedPattern[] transformedPatterns;
    final int priority;

    public int getPriority() {
        return priority;
    }

    public ActionDefinition(String name, Class<? extends ScriptAction> cls, int priority, @Nullable Feature... features) throws Exception {
        this.name = name;
        this.cls=cls;
        this.priority=priority;
        if(features!=null){
            this.features = features;
            this.transformedPatterns = new TransformedPattern[this.features.length];
            for(int i = 0; i<this.features.length; i++){
                try {
                    this.transformedPatterns[i]=ScriptDecoder.transformPattern(this.features[i].pattern());
                    //System.out.println("Set transformed patterns for "+getActionClass()+" : "+ Arrays.toString(transformedPatterns));
                    //System.out.println("Regex for "+features[i]+" is : "+transformedPatterns[i].getPattern().pattern());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }

    }

    //[0] : index
    //[1] : marks
    public int[] getMatchedPatternIndexAndMarks(String line){
        for (int i = 0; i < transformedPatterns.length ; i++) {
            //System.out.println("Checking if "+line+" matches "+transformedPatterns[i].getPattern().pattern());
            Matcher m = transformedPatterns[i].getPattern().matcher(line);
            if(m.matches()){
                m.reset();
                boolean found = m.find();
                //System.out.println("Find = "+found);
                return new int[]{i,found?transformedPatterns[i].getAllMarks(line):0};
            }
        }
        return new int[0];
    }

    public Class<? extends ScriptAction> getActionClass() {
        return cls;
    }

    public Class<? extends ScriptAction> cls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Feature[] getFeatures() {
        return features;
    }

}
