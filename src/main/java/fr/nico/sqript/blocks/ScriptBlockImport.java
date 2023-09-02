package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptInstance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Block(
        feature = @Feature(name = "Import",
                description = "Import a bunch of functions from another script file.",
                examples = "import:\n" +
                        "    * from my_script",
                regex = "^import(?:s)?:\\s*")
        )
public class ScriptBlockImport extends ScriptBlock {

    public ScriptBlockImport(ScriptToken head) throws ScriptException.ScriptSyntaxException {
        super(head);
    }

    @Override
    public void init(ScriptLineBlock scriptLineBlock) throws Exception {
        Pattern p = Pattern.compile("\\s+(.*) from (.*)");
        for(ScriptToken s : scriptLineBlock.getContent()){
            Matcher m = p.matcher(s.getText());
            if(m.find()){
                String function = m.group(1);
                String script = m.group(2);
                ScriptInstance from = ScriptManager.getScriptFromName(script);
                if(from!=null){
                    ScriptFunctionalBlock imported;
                    if(function.equals("*")){
                        getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class).addAll(from.getBlocksOfClass(ScriptBlockFunction.class));
                        getScriptInstance().getBlocksOfClass(ScriptBlockPacket.class).addAll(from.getBlocksOfClass(ScriptBlockPacket.class));
                    }else if((imported=from.getFunction(function))!=null){
                        getScriptInstance().getBlocksOfClass(ScriptBlockFunction.class).add(imported);
                    }else{
                        throw new ScriptException.ScriptUnknownFunctionException(s.with(function));
                    }
                }else{
                    throw new ScriptException.ScriptUnknownInstanceException(s.with(script));
                }
            }
        }
    }
}
