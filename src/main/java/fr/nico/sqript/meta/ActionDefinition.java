package fr.nico.sqript.meta;

import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

public class ActionDefinition {

    String name;
    String[] description;
    String[] patterns;
    String[] example;
    Side side;
    public int getPriority() {
        return priority;
    }

    final int priority;

    public TransformedPattern[] transformedPatterns;

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

    public Side getSide() {
        return side;
    }

    public Class<? extends ScriptAction> getActionClass() {
        return cls;
    }

    public Class<? extends ScriptAction> cls;

    public ActionDefinition(String name, String[] description, String[] example, Class<? extends ScriptAction> cls, int priority, Side side, @Nullable String... patterns) {
        this.name = name;
        this.example = example;
        this.description = description;
        this.cls=cls;
        this.priority=priority;
        this.side=side;
        if(patterns!=null){
            this.patterns = patterns;
            this.transformedPatterns = new TransformedPattern[patterns.length];
            for(int i = 0;i<this.patterns.length;i++){
                try {
                    this.transformedPatterns[i]=ScriptDecoder.transformPattern(patterns[i]);
                    //System.out.println("Regex for "+patterns[i]+" is : "+transformedPatterns[i].getRegex());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getExample() {
        return example;
    }

    public void setExample(String[] example) {
        this.example = example;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String... patterns) {
        this.patterns = patterns;
    }
}
