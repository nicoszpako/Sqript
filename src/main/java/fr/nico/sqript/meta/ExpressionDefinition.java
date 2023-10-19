package fr.nico.sqript.meta;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.ScriptParameterDefinition;
import fr.nico.sqript.structures.TransformedPattern;

import javax.xml.bind.SchemaOutputResolver;
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
    public MatchResult[] getMatchResults(String line) {
        List<MatchResult> matchResults = new ArrayList<>();
        t:
        for (int i = 0; i < transformedPatterns.length; i++) {
            TransformedPattern pattern = transformedPatterns[i];
            Matcher m = pattern.getPattern().matcher(line);
            //System.out.println("Does "+line+" matches "+pattern.getPattern().pattern());
            if (m.matches()) {
                //System.out.println(m.matches()+ " for pattern "+pattern.getPattern().pattern()+" matching "+line);
                for (String argument : pattern.getAllArguments(line)) {
                    if (!ScriptDecoder.isParenthesageGood(argument))
                        continue t;
                }
                //System.out.println("Added !");
                matchResults.add(new MatchResult(i, transformedPatterns[i].getAllMarks(line)));
            }
        }
        if (!matchResults.isEmpty()) {
            return matchResults.toArray(new MatchResult[0]);
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
