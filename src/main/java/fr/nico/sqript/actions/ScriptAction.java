package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ScriptAction extends IScript {

    public List<ScriptExpression> getParameters() {
        return parameters;
    }

    public ScriptExpression getParameter(int index){

        return getParameters().get(index-1);
    }

    public Object getParameter(int index, ScriptContext context) throws ScriptException {
        return getParameters().get(index-1).get(context).getObject();
    }

    public <T> T getParameterOrDefault(ScriptExpression parameter, T defaultValue, ScriptContext context) throws ScriptException {
        return parameter == null ? defaultValue : (T) parameter.get(context).getObject();
    }

    public void setParameters(List<ScriptExpression> parameters) {
        this.parameters = parameters;
    }

    public int getParametersSize() {
        return parameters.size();
    }

    private List<ScriptExpression> parameters;

    public int getMatchedIndex() {
        return matchedIndex;
    }

    private int matchedIndex;

    private int marks;

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getMarks() {
        return marks;
    }

    public boolean getMarkValue(int mark){
        return marks >> mark == 1;
    }



    public void setMatchedIndex(int matchedIndex) {
        this.matchedIndex = matchedIndex;
    }

    public ScriptAction(){}

    @Override
    public abstract void execute(ScriptContext context) throws ScriptException;

    public void build(ScriptLine line, ScriptCompileGroup compileGroup, List<String> parameters, int matchedIndex, int marks) throws Exception {
        List<ScriptExpression> expressions = new ArrayList<>(parameters.size());
        String[] strings = ScriptDecoder.extractStrings(line.text);
        //System.out.println("Building action for line : "+line+", parameters are :"+ Arrays.toString(parameters.toArray(new String[0])));
        //System.out.println("for line : "+line+" marks are : "+Integer.toBinaryString(marks));
        for (String parameter : parameters) {
            //System.out.println("Processing parameter : "+parameter);
            if(parameter==null) {
                expressions.add(null);
                continue;
            }
            ScriptExpression e = ScriptDecoder.getExpression(line.with(parameter),compileGroup, strings);
            if (e != null)
                expressions.add(e);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line.with(parameter).withString(strings));
            }
        }
        setParameters(expressions);
        setMatchedIndex(matchedIndex);
        System.out.println("Settign line to "+line);
        setLine(line);
        setMarks(marks);
    }
}
