package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeMessagePrototype;
import net.minecraftforge.fml.relauncher.SideOnly;


@Block(name = "message",
        description = "Network messages blocks",
        examples = "message frame1_messages(parameter):",
        regex = "^message .*",
        side = Side.BOTH,
        fields = {"client","server"}
)
public class ScriptBlockMessage extends ScriptFunctionalBlock {


    IScript client = null;
    IScript server = null;

    public IScript getClient() {
        return client;
    }

    public IScript getServer() {
        return server;
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        return new TypeMessagePrototype(new ScriptMessage(this.name, parameters));
    }

    public ScriptBlockMessage(ScriptLine head) throws ScriptException {
        super(head);
    }

    @Override
    protected void load() throws Exception {
        ScriptCompileGroup group = createCompileGroup();
        if(fieldDefined("client")){
            client = getSubBlock("client").compile(group);
        }
        if(fieldDefined("server")){
            server = getSubBlock("server").compile(group);
        }
        ScriptNetworkManager.registerMessage(this);
    }

}
