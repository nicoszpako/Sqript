package fr.nico.sqript.meta;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

public class ExpressionDefinition {

    String name;
    String[] description;
    String[] patterns;
    String[] example;
    final int priority;
    public TransformedPattern[] transformedPatterns;

    //[0] : pattern index
    //[1] : pattern index relative position in match
    //[2] : marks
    public int[] getMatchedPatternIndexAndPosition(String line){
        for (int i = 0; i < transformedPatterns.length ; i++) {
            //System.out.println("Checking if "+line+" matches "+transformedPatterns[i].getPattern());
            Matcher m = transformedPatterns[i].getPattern().matcher(line);
            if(m.matches()){
                if(m.groupCount()>0){
                    return new int[]{i,m.end(1), transformedPatterns[i].getAllMarks(m)};
                }else{
                    return new int[]{i,line.length()+1, transformedPatterns[i].getAllMarks(m)};
                }
            }

        }
        return null;
    }


    private final Class<? extends ScriptExpression> cls;

    public int getPriority() {
        return priority;
    }

    public Class<? extends ScriptExpression> getExpressionClass() {
        return cls;
    }

    public ExpressionDefinition(String name, String[] description, String[] example, Class<? extends ScriptExpression> cls, int priority, @Nullable String... patterns) {
        this.name = name;
        this.example = example;
        this.description = description;
        this.cls=cls;
        this.priority=priority;
        if(patterns!=null){
            this.patterns = patterns;
            this.transformedPatterns = new TransformedPattern[patterns.length];
            for(int i = 0;i<this.patterns.length;i++){
                try {
                    String[] t = ScriptDecoder.splitAtDoubleDot(patterns[i]);
                    this.transformedPatterns[i]=ScriptDecoder.transformPattern(t[0]);
                    //System.out.println("Pattern "+i+" : "+this.transformedPatterns[i].getPattern().pattern());
                } catch (Exception e) {
                    ScriptManager.log.info("Error trying to build expression : "+name);
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
