package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Subclass of ScriptBlock allow to easily create function-like structured blocks.
 */
public class ScriptFunctionalBlock extends ScriptBlock {

    /**
     * Execute the IScript associated to the function and return the value
     */
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        //System.out.println("Launching function "+name+" with parameters : "+ Arrays.toString(parameters));
        ScriptContext functionContext = new ScriptContext(context);
        functionContext.setReturnValue(new ScriptTypeAccessor(null, ""));

        wrapParametersInContext(functionContext, parameters);
        //System.out.println("Passed context : "+functionContext);
        //System.out.println("Script tree of wrapped is : ");
        //ScriptLoader.dispScriptTree(wrapped,0);
        ScriptClock clock = new ScriptClock(functionContext);
        clock.start(getRoot());
        //System.out.println("Returned value is : "+functionContext.returnValue);
        return functionContext.getReturnValue().element;
    }

    public void wrapParametersInContext(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException.ScriptNotEnoughArgumentException {
        if (parameters.length < this.parameters.length) {
            throw new ScriptException.ScriptNotEnoughArgumentException(getLine());
        }
        for (int i = 0; i < this.parameters.length; i++) {
            context.put(new ScriptTypeAccessor(parameters[i], this.parameters[i]));
        }
    }

    public ScriptCompilationContext createCompileGroup() throws ScriptException.ScriptNotEnoughArgumentException {
        ScriptCompilationContext compileGroup = new ScriptCompilationContext();
        if (parameters.length > 0)
            for (String s : parameters) {
                if (!s.isEmpty())
                    compileGroup.add(s);
            }
        return compileGroup;
    }


    public String name;

    public String[] parameters;

    public ScriptFunctionalBlock(ScriptToken head) throws ScriptException {
        //System.out.println("Loading function : "+head.text);
        super(head);
        head = head.with(head.getText().replaceFirst(this.getClass().getAnnotation(Block.class).feature().name() + "\\s+", ""));
        Pattern p = Pattern.compile("\\s*^([\\w ]*)\\((.*)\\)\\s*:");
        Matcher m = p.matcher(head.getText());
        if (m.find()) {
            name = m.group(1);
            String params = m.group(2);
            if (!params.isEmpty()) {
                parameters = params.split(",");
            } else {
                parameters = new String[0];
            }
        }
    }

    @Override
    public void init(ScriptLineBlock block) throws Exception {
        super.init(block);
        System.out.println("Loaded function : " + name);
        setRoot(getMainField().compile());
        //System.out.println(getRoot()==null);
        getScriptInstance().registerBlock(this);
    }
}