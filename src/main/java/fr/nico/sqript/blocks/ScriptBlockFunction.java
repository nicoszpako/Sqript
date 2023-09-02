package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.Side;

@Block(
        feature = @Feature(name = "Function",
                description = "Define a piece of code that can be called in order to do some action or compute a result to return.",
                examples = "function broadcast({message}): #Sends a message to all players\n" +
                        "    for {p} in all players:\n" +
                        "        send {message} to {p}",
                regex = "^function .*"),
        fields = {@Feature(name = "side")}

)
public class ScriptBlockFunction extends ScriptFunctionalBlock {

    public Side side;

    public ScriptBlockFunction(ScriptToken head) throws ScriptException {
        super(head);
    }

    @Override
    public void init(ScriptLineBlock block) throws Exception {
        groupFields(block.getContent());

        if(fieldDefined("side"))
            side = Side.from(getSubBlock("side").getRawContent());
        //System.out.println("Loading function : "+getLine());
        if (side != null && !side.isValid()) {
            System.out.println("Side was not valid for the block");
            return;
        }
        getScriptInstance().registerBlock(this);
        //System.out.println("Loaded function : " + name);
        setRoot(getMainField().compile());
        load();
    }
}