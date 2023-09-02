package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLoader;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeMessagePrototype;
import fr.nico.sqript.types.TypePlayer;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;


@Block(
        feature = @Feature(name = "Packet",
                description = "Define a packet of data that can be sent to a player or to the server. Packets will transmit data only if the data's type is serialisable. Packets are used to transmit information between players (clients) and the server, they are useful in GUIs for example, to tell the server whenever the client clicks on a button (otherwise, as this happens on the player's computer, the server can't know it). Be careful when using packets, and always try to check if the information is verified, because anyone can send a packet with false information in order to cheat or to mess with the server. For example, checking distances can be good when a player asks the server to open an inventory.",
                examples = "packet test_packet({message}):\n" +
                        "\tclient:\n" +
                        "\t\tprint \"client side print:\" + {message}\n" +
                        "\tserver:\n" +
                        "\t\tprint \"server side print:\" + {message} + \"sent by\" + player\n" +
                        "\n" +
                        "command /sendPacket <string>:\n" +
                        "\tsend test_packet(arg-1) to player",
                regex = "^packet .*",
                side = Side.BOTH),
        fields = {
                @Feature(name = "client"),
                @Feature(name = "server")
        }
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

    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        return new TypeMessagePrototype(new ScriptMessage(this.name, parameters));
    }

    public ScriptBlockPacket(ScriptToken head) throws ScriptException {
        super(head);
    }

    @Override
    public void displayTree(int i) {
        String tab = "";
        for (int j = 0; j < i; j++) tab += "|    ";
        ScriptManager.log.info(tab+""+name+" "+ Arrays.toString(parameters));
        tab += "|    ";
        if (client != null) {
            ScriptManager.log.info(tab+"client :");
            ScriptLoader.dispScriptTree(client, i + 2);
        }
        if (server != null) {
            ScriptManager.log.info(tab + "server :");
            ScriptLoader.dispScriptTree(server,i + 2);
        }
    }

    @Override
    protected void load() throws Exception {
        //On enregistre le message fraîchement créé, pour qu'il soit reconnu par le reste du script.
        ScriptNetworkManager.registerMessage(this);

        //Compilation des fields "client" et "server"
        if(fieldDefined("client") && Side.CLIENT.isEffectivelyValid()){
            client = getSubBlock("client").compile();
        }
        if(fieldDefined("server") && Side.SERVER.isEffectivelyValid()){
            ScriptCompilationContext compilationContext = new ScriptCompilationContext();
            compilationContext.add("(player|sender)", TypePlayer.class);
            server = getSubBlock("server").compile(compilationContext);
        }


    }

}
