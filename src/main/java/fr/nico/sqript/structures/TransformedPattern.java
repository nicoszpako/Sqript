package fr.nico.sqript.structures;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformedPattern {

    Pattern pattern;

    int marksCount; //Number of (¦n) marks in the expression
    int argsCount; //Number of {type} arguments in the expression


    /**
     * Whether this pattern can match any expression. In this case it will be checked only if types are *strictly* matching.
     */
    boolean greedy;

    /**
     * The return type of this pattern.
     */
    private Class<? extends ScriptElement> returnType;

    /**
     * The parameters definitions of each defined parameters in this pattern. Ex: a {item|block} to {player} will have a definition of [[item,block], [player]]
     */
    ScriptParameterDefinition[][] parameterDefinitions = new ScriptParameterDefinition[0][0];

    public TransformedPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public TransformedPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public int getArgsCount() {
        return argsCount;
    }

    public Class[] getValidTypes(int parameterIndex) {
        parameterIndex = Math.min(parameterIndex, parameterDefinitions.length - 1);
        Class[] validTypes = new Class[parameterDefinitions[parameterIndex].length];
        for (int i = 0; i < parameterDefinitions[parameterIndex].length; i++) {
            validTypes[i] = parameterDefinitions[parameterIndex][i].getTypeClass();
        }
        return validTypes;
    }

    public TransformedPattern(Pattern pattern, int marksCount, int argsCount) {
        this.pattern = pattern;
        this.marksCount = marksCount;
        this.argsCount = argsCount;
    }

    public TransformedPattern(String pattern, int marksCount, int argsCount, ScriptParameterDefinition[][] parameterDefinitions) {
        this.pattern = Pattern.compile(pattern);
        this.marksCount = marksCount;
        this.argsCount = argsCount;
        this.parameterDefinitions = parameterDefinitions;
    }


    public boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public ScriptParameterDefinition[][] getTypes() {
        return parameterDefinitions;
    }

    /**
     * Returns a binary sequence on which each nth bit will be 1 if the nth mark is validated
     * @param line The line to check marks
     * @return
     */
    public int getAllMarks(String line){
        //System.out.println("Getting all marks for : "+line);
        //System.out.println("Pattern is : "+getPattern().pattern());
        int r = 0;
        Matcher m = getPattern().matcher(line);
        m.find();
        //System.out.println("Mark count is : "+marksCount);

        for (int i = 1; i <= marksCount; i++) {
            try{
                //System.out.println("Checking for mark : "+i);
                if(m.group("m"+i)!=null)
                {
                    //System.out.println("m"+i+" group is not null");
                    r = r | (1 << i);
                    //System.out.println(Integer.toBinaryString(r));
                }
            }catch(Exception ignored){
                ignored.printStackTrace();
            }
        }
        //System.out.println("Marks are : "+ Integer.toBinaryString(r));
        return r;
    }

    /**
     * Extracts the arguments of a String using this pattern.
     * @param match
     * @return
     */
    public String[] getAllArguments(String match){
        String[] r = new String[getArgsCount()];
        Matcher m = pattern.matcher(match);
        //System.out.println("Getting all arguments for : "+match+" as "+pattern.pattern());
        if (m.find()) {
            //System.out.println("Found");
            for (int i = 0; i < argsCount; i++) {
                String s = null;
                try {
                    s = m.group("a" + i);
                    //System.out.println("Adding : "+s);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                r[i] = s;
            }
        }
        return r;
    }

    public void setReturnType(Class<? extends ScriptElement> type) {
        this.returnType = type;
    }

    public  Class<? extends ScriptElement> getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "TransformedPattern{" +
                "pattern=" + pattern +
                ", returnType=" + returnType +
                ", parameterDefinitions=" + Arrays.deepToString(parameterDefinitions) +
                '}';
    }
}
