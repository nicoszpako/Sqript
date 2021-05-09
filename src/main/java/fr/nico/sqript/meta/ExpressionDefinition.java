package fr.nico.sqript.meta;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.structures.TransformedPattern;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

public class ExpressionDefinition {

    final int priority;
    private final Class<? extends ScriptExpression> cls;
    public TransformedPattern[] transformedPatterns;
    String name;
    String[] description;
    String[] patterns;
    String[] example;
    Side side;

    public ExpressionDefinition(String name, String[] description, String[] example, Class<? extends ScriptExpression> cls, int priority, Side side, @Nullable String... patterns) {
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

    //[0] : pattern index
    //[1] : pattern index relative position in match
    //[2] : marks
    public int[] getMatchedPatternIndexAndPosition(String line){
        int shortestIndex = -1;
        int shortestPosition = -1;
        t:for (int i = 0; i < transformedPatterns.length ; i++) {
            //System.out.println("Checking if "+line+" matches "+transformedPatterns[i].getPattern());
            Matcher m = transformedPatterns[i].getPattern().matcher(line);
            if(m.matches()){
                for(String argument : transformedPatterns[i].getAllArguments(line)){
                    if(!ScriptDecoder.isParenthesageGood(argument))
                        continue t;
                }
                if(m.groupCount()>0){
                    int leastGroupStartPosition = -1;
                    for (int j = 1; j < m.groupCount()+1; j++) {
                        if (m.start(j) != -1) {
                            leastGroupStartPosition = m.start(j);
                            break;
                        }
                    }
                    //System.out.println("OK : "+this.name+":"+i+" ("+transformedPatterns[i].getPattern()+") with position "+leastGroupStartPosition+" (actual shortest position : "+shortestPosition+")");
                    if (leastGroupStartPosition > shortestPosition) {
                        shortestPosition = leastGroupStartPosition;
                        shortestIndex = i;
                    }
                }else{
                    if (line.length()+1 > shortestPosition) {
                        shortestPosition = line.length()+1;
                        shortestIndex = i;
                    }
                }
            }

        }
        if(shortestIndex != -1){
            //System.out.println("Returning : "+this.name+":"+shortestIndex+" ("+transformedPatterns[shortestIndex].getPattern()+") for "+line);
            return new int[]{shortestIndex,shortestPosition, transformedPatterns[shortestIndex].getAllMarks(line)};
        }else
            return null;

    }

    public Side getSide() {
        return side;
    }

    public int getPriority() {
        return priority;
    }

    public Class<? extends ScriptExpression> getExpressionClass() {
        return cls;
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
