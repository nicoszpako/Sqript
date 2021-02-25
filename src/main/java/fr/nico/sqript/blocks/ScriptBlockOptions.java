package fr.nico.sqript.blocks;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.ScriptInstance;
import fr.nico.sqript.compiling.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Block(name = "options",
        description = "Options block",
        examples = "options:",
        regex = "^options:\\s*")
public class ScriptBlockOptions extends ScriptBlock {

    public ScriptBlockOptions(ScriptLine head) throws ScriptException.ScriptSyntaxException {
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        Pattern p = Pattern.compile("^\\s+(.*?)\\s*:\\s*(.*)$");
        for(ScriptLine s : scriptLineBlock.getContent()){
            Matcher m = p.matcher(s.text);
            if(m.find()){
                String optionName = m.group(1);
                if(!optionName.matches("[a-zA-Z0-9_]*")){
                    throw new ScriptException.ScriptSyntaxException(s,"The option's key must only contain letters, numbers and underscores");
                }
                String optionValue = m.group(2);
                ScriptExpression value = null;
                ScriptCompileGroup compileGroup = new ScriptCompileGroup();
                if((value= ScriptDecoder.getExpression(s.with(optionValue),compileGroup))!=null){
                    getScriptInstance().getOptions().put(optionName,value);
                }else{
                    throw new ScriptException.ScriptUnknownExpressionException(s.with(optionValue));
                }
            }
        }
    }
}
