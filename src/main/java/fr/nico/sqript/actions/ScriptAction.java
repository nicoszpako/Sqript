package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.ActionDefinition;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;

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

    public String getMatchedName() {
        return this.getClass().getAnnotation(Action.class).features()[getMatchedIndex()].name();
    }

    public void setMatchedIndex(int matchedIndex) {
        this.matchedIndex = matchedIndex;
    }

    public ScriptAction(){}

    @Override
    public abstract void execute(ScriptContext context) throws ScriptException;

    public void build(ScriptToken line, ScriptCompilationContext compileGroup, List<String> parameters, int matchedIndex, int marks) throws Exception {
        List<ScriptExpression> expressions = new ArrayList<>(parameters.size());
        System.out.println("Building action for line : "+line+", parameters are :"+ Arrays.toString(parameters.toArray(new String[0])));
        //System.out.println("Marks are : "+Integer.toBinaryString(marks));
        String[] strings = ScriptDecoder.extractStrings(line.getText());
        //System.out.println("for line : "+line+" marks are : "+Integer.toBinaryString(marks));
        ActionDefinition actionDefinition = ScriptManager.getDefinitionFromAction(this.getClass());

        for (int i = 0; i < parameters.size() ; i++) {
            //System.out.println("Processing parameter : "+parameter);
            String parameter = parameters.get(i);
            if(parameter==null) {
                expressions.add(null);
                continue;
            }
            //System.out.println(matchedIndex+" "+ i+" "+Arrays.toString(actionDefinition.transformedPatterns));
            //
            ScriptExpression e = ScriptDecoder.parse(line.with(parameter),compileGroup);
            if (e != null)
                expressions.add(e);
            else {
                throw new ScriptException.ScriptUnknownExpressionException(line.with(parameter).withString(strings));
            }
        }
        setParameters(expressions);
        setMatchedIndex(matchedIndex);
        //System.out.println("Settign line to "+line);
        setLine(line);
        setMarks(marks);
    }
}
