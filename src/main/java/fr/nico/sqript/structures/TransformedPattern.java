package fr.nico.sqript.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformedPattern {

    Pattern pattern;

    int marksCount; //Number of (¦n) marks in the expression
    int argsCount; //Number of {type} arguments in the expression

    ScriptParameterDefinition[] parameterDefinitions = new ScriptParameterDefinition[0];

    public Pattern getPattern() {
        return pattern;
    }

    public ScriptParameterDefinition[] getTypes() {
        return parameterDefinitions;
    }

    public TransformedPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public TransformedPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public int getAllMarks(Matcher m){
        int r = 0;
        //System.out.println("argsCount = "+marksCount);
        for (int i = 0; i < marksCount; i++) {
            try{
                if(m.group("m"+i)!=null)
                {
                    r = r | (1 << i);
                    //System.out.println("r="+r);
                }
            }catch(Exception ignored){}
        }
        return r;
    }

    public String[] getAllArguments(String match){
        List<String> r = new ArrayList<>();
        Matcher m = pattern.matcher(match);
        //System.out.println("Match : "+match);
        //System.out.println("Pattern : "+pattern.pattern());
        //System.out.println("ArgsCount : "+argsCount);
        //System.out.println("Pattern is : "+pattern.pattern());
        if(m.find())
            for (int i = 0; i < argsCount; i++) {
                String s = null;
                try {
                    s = m.group("a"+i);
                }catch(Exception ignored){
                    ignored.printStackTrace();
                }
                r.add(s);
            }
        return r.toArray(new String[0]);
    }

    public TransformedPattern(Pattern pattern, int marksCount, int argsCount) {
        this.pattern = pattern;
        this.marksCount = marksCount;
        this.argsCount = argsCount;
    }

    public TransformedPattern(String pattern, int marksCount, int argsCount, ScriptParameterDefinition[] parameterDefinitions) {
        this.pattern = Pattern.compile(pattern);
        this.marksCount = marksCount;
        this.argsCount = argsCount;
        this.parameterDefinitions = parameterDefinitions;
    }
}
