package fr.nico.sqript.blocks;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.compiling.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Block(name = "options",
        description = "Options block",
        examples = "options:",
        regex = "^options:\\s*")
public class ScriptBlockOptions extends ScriptBlock {

    public ScriptBlockOptions(ScriptToken head) throws ScriptException.ScriptSyntaxException {
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        Pattern p = Pattern.compile("^\\s+(.*?)\\s*:\\s*(.*)$");
        for(ScriptToken s : scriptLineBlock.getContent()){
            Matcher m = p.matcher(s.getText());
            if(m.find()){
                String optionName = m.group(1);
                if(!optionName.matches("[a-zA-Z0-9_]*")){
                    throw new ScriptException.ScriptSyntaxException(s,"The option's key must only contain letters, numbers and underscores");
                }
                String optionValue = m.group(2);
                ScriptExpression value;
                if((value= ScriptDecoder.parseExpression(s.with(optionValue),new ScriptCompileGroup()))!=null){
                    getScriptInstance().getOptions().put(optionName,value);
                }else{
                    throw new ScriptException.ScriptUnknownExpressionException(s.with(optionValue));
                }
            }
        }
    }
}
