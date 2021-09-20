package fr.nico.sqript.blocks;

import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.meta.Feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Block(
        feature = @Feature(name = "Options",
                description = "Define options in your script to give it more flexibility and configuration.",
                examples = "options:\n" +
                        "    my_value: 5",
                regex = "^options:\\s*")
)
public class ScriptBlockOptions extends ScriptBlock {

    public ScriptBlockOptions(ScriptToken head) throws ScriptException.ScriptSyntaxException {
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        Pattern p = Pattern.compile("^\\s+(.*?)\\s*:\\s*(.*)$");
        for (ScriptToken s : scriptLineBlock.getContent()) {
            Matcher m = p.matcher(s.getText());
            if (m.find()) {
                String optionName = m.group(1);
                if (!optionName.matches("[a-zA-Z0-9_]*")) {
                    throw new ScriptException.ScriptSyntaxException(s, "The option's key must only contain letters, numbers and underscores");
                }
                String optionValue = m.group(2);
                ScriptExpression value = ScriptDecoder.parse(s.with(optionValue), new ScriptCompilationContext());
                getScriptInstance().getOptions().put(optionName, value);
            }
        }
    }
}
