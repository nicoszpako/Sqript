package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptInstance;
import fr.nico.sqript.types.ScriptType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScriptFunctionalBlock extends ScriptBlock {

    //Execute the IScript associated to the function and return the value
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        //System.out.println("Launching function "+name+" with parameters : "+ Arrays.toString(parameters));
        ScriptContext functionContext = new ScriptContext(context);
        functionContext.returnValue = new ScriptAccessor(null,"");

        wrapParametersInContext(functionContext,parameters);
        //System.out.println("Passed context : "+functionContext);
        //System.out.println("Script tree of wrapped is : ");
        //ScriptLoader.dispScriptTree(wrapped,0);
        ScriptClock clock = new ScriptClock(functionContext);
        clock.start(getRoot());
        //System.out.println("Returned value is : "+functionContext.returnValue);
        return functionContext.returnValue.element;
    }

    public void wrapParametersInContext(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException.ScriptNotEnoughArgumentException {
        if(parameters.length < this.parameters.length){
            throw new ScriptException.ScriptNotEnoughArgumentException(getLine());
        }
        for (int i = 0; i < this.parameters.length; i++) {
            context.put(new ScriptAccessor(parameters[i], this.parameters[i]));
        }
    }

    public ScriptCompileGroup createCompileGroup() throws ScriptException.ScriptNotEnoughArgumentException {
        ScriptCompileGroup compileGroup = new ScriptCompileGroup();
        if (parameters.length > 0)
            for (String s : parameters) {
                if (!s.isEmpty())
                    compileGroup.add(s);
            }
        return compileGroup;
    }


    public String name;


    public String[] parameters;

    public ScriptFunctionalBlock(ScriptLine head) throws ScriptException {
        //System.out.println("Loading function : "+head.text);
        super(head);
        head = head.with(head.text.replaceFirst(this.getClass().getAnnotation(Block.class).name()+"\\s+", ""));
        Pattern p = Pattern.compile("\\s*^([\\w ]*)\\((.*)\\)\\s*:");
        Matcher m = p.matcher(head.text);
        if(m.find()){
            name = m.group(1);
            String params = m.group(2);
            if(!params.isEmpty()){
                parameters = params.split(",");
            }else{
                parameters = new String[0];
            }
        }
    }


    @Override
    public void init(ScriptLineBlock block) throws Exception {
        super.init(block);
        System.out.println("Loaded function : "+name);
        setRoot(getMainField().compile());
        //System.out.println(getRoot()==null);
        getScriptInstance().registerBlock(this);
    }
}