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


@Block(name = "packet",
        description = "Network packet blocks",
        examples = "packet frame1_packet(parameter):",
        regex = "^packet .*",
        side = Side.BOTH,
        fields = {"client","server"}
)
public class ScriptBlockPacket extends ScriptFunctionalBlock {

    //Les IScript à exécuter en fonction du side de réception
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

    public ScriptBlockPacket(ScriptLine head) throws ScriptException {
        super(head);
    }

    @Override
    protected void load() throws Exception {
        ScriptCompileGroup group = createCompileGroup();

        //Compilation des fields "client" et "server"
        if(fieldDefined("client")){
            client = getSubBlock("client").compile(group);
        }
        if(fieldDefined("server")){
            server = getSubBlock("server").compile(group);
        }

        //On enregistre le message fraîchement créé, pour qu'il soit reconnu par le reste du script.
        ScriptNetworkManager.registerMessage(this);
    }

}
