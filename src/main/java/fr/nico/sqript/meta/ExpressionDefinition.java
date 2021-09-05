package fr.nico.sqript.meta;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptParameterDefinition;
import fr.nico.sqript.structures.TransformedPattern;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ExpressionDefinition {

    final int priority;
    private final Class<? extends ScriptExpression> cls;
    public TransformedPattern[] transformedPatterns;
    String name;
    Feature[] features;

    public ExpressionDefinition(String name, Class<? extends ScriptExpression> cls, int priority, Feature... features) throws Exception {
        this.name = name;
        this.cls = cls;
        this.priority = priority;
        if (features != null) {
            this.features = features;
            this.transformedPatterns = new TransformedPattern[features.length];
            for (int i = 0; i < features.length; i++) {
                try {
                    String[] t = ScriptDecoder.splitAtDoubleDot(features[i].pattern());
                    this.transformedPatterns[i] = ScriptDecoder.transformPattern(t[0]);
                    this.transformedPatterns[i].setReturnType(ScriptDecoder.parseType(features[i].type()));
                    //System.out.println("Pattern "+i+" : "+this.transformedPatterns[i].getPattern().pattern());
                } catch (Exception e) {
                    ScriptManager.log.info("Error trying to build expression : " + name);
                    e.printStackTrace();
                    throw e;
                }
            }
        }

    }

    //[0] : pattern index
    //[1] : pattern index relative position in match
    //[2] : marks
    public MatchResult getMatchResult(String line) {
        int shortestIndex = -1;
        int shortestPosition = -1;
        t:
        for (int i = 0; i < transformedPatterns.length; i++) {
            TransformedPattern pattern = transformedPatterns[i];
            //System.out.println("Class : "+cls);
            Matcher m = pattern.getPattern().matcher(line);
            if (m.matches()) {
                //System.out.println(getExpressionClass()+" "+i+" possibleTypes : "+possibleTypes+" returnType: "+pattern.getReturnType());
                if (m.matches()) {
                    for (String argument : pattern.getAllArguments(line)) {
                        if (!ScriptDecoder.isParenthesageGood(argument))
                            continue t;
                    }
                    if (m.groupCount() > 0) {
                        int leastGroupStartPosition = -1;
                        for (int j = 1; j < m.groupCount() + 1; j++) {
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
                    } else {
                        if (line.length() + 1 > shortestPosition) {
                            shortestPosition = line.length() + 1;
                            shortestIndex = i;
                        }
                    }
                }
            }


        }
        if (shortestIndex != -1) {
            //System.out.println("Returning : "+this.name+":"+shortestIndex+" ("+transformedPatterns[shortestIndex].getPattern()+") for "+line);
            return new MatchResult(shortestIndex, shortestPosition, transformedPatterns[shortestIndex].getAllMarks(line));
        } else
            return null;

    }

    public ScriptExpression instanciate(ScriptToken line, int matchedIndex, int marks) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ScriptExpression instance = getExpressionClass().getConstructor().newInstance();
        instance.setMatchedIndex(matchedIndex);
        instance.setMarks(marks);
        instance.setLine((ScriptToken) line.clone());
        instance.setReturnType(transformedPatterns[matchedIndex].getReturnType());
        return instance;
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

    public Feature[] getFeatures() {
        return features;
    }
}
