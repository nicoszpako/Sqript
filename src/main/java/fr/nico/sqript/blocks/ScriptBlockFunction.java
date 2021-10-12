package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;

@Block(
        feature = @Feature(name = "Function",
                description = "Define a piece of code that can be called in order to do some action or compute a result to return.",
                examples = "function broadcast({message}): #Sends a message to all players\n" +
                        "    for {p} in all players:\n" +
                        "        send {message} to {p}",
                regex = "^function .*")
)
public class ScriptBlockFunction extends ScriptFunctionalBlock {


    public ScriptBlockFunction(ScriptToken head) throws ScriptException {
        super(head);
    }

}