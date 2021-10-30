package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.ScriptClock;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

import java.util.Arrays;
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
        ScriptContext functionContext = new ScriptContext(context);
        functionContext.setReturnValue(new ScriptTypeAccessor(null, ""));

        wrapParametersInContext(functionContext, parameters);

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

    public String name;

    public String[] parameters;

    public ScriptFunctionalBlock(ScriptToken head) throws ScriptException {
        super(head);
        //System.out.println("Loading function : "+head);
        head = head.with(head.getText().replaceFirst(this.getClass().getAnnotation(Block.class).feature().name().toLowerCase() + "\\s+", ""));
        Pattern p = Pattern.compile("\\s*^([\\w ]*)\\((.*)\\)\\s*:");
        Matcher m = p.matcher(head.getText());
        if (m.find()) {
            name = m.group(1);
            String params = m.group(2);
            if (!params.isEmpty()) {
                parameters = params.split(",");
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = parameters[i].trim();
                }
            } else {
                parameters = new String[0];
            }
        }

    }

    @Override
    public void init(ScriptLineBlock block) throws Exception {
        groupFields(block.getContent());
        getScriptInstance().registerBlock(this);
        //System.out.println("Loaded function : " + name);
        setRoot(getMainField().compile());
        load();
        //System.out.println(getRoot()==null);
    }
}