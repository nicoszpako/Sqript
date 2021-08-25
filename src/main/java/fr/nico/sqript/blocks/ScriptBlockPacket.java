package fr.nico.sqript.blocks;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeMessagePrototype;
import net.minecraftforge.fml.relauncher.SideOnly;


@Block(
        feature = @Feature(name = "Packet",
                description = "Define a packet of data that can be sent to a player or to the server. Packets will transmit data only if the data's type is serialisable. Packets are used to transmit information between players (clients) and the server, they are useful in GUIs for example, to tell the server whenever the client clicks on a button (otherwise, as this happens on the player's computer, the server can't know it). Be careful when using packets, and always try to check if the information is verified, because anyone can send a packet with false informations in order to cheat or to mess with the server. For example, checking distances can be good when a player asks the server to open an inventory. ",
                examples = "packet test_packet(message):\n" +
                        "\tclient:\n" +
                        "\t\tprint message\n" +
                        "\tserver:\n" +
                        "\t\tprint message\n" +
                        "\n" +
                        "command /sendPacket {string}:\n" +
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

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override
    public ScriptType get(ScriptContext context, ScriptType<?>[] parameters) throws ScriptException {
        return new TypeMessagePrototype(new ScriptMessage(this.name, parameters));
    }

    public ScriptBlockPacket(ScriptToken head) throws ScriptException {
        super(head);
    }

    @Override
    protected void load() throws Exception {

        //Compilation des fields "client" et "server"
        if(fieldDefined("client")){
            client = getSubBlock("client").compile();
        }
        if(fieldDefined("server")){
            server = getSubBlock("server").compile();
        }

        //On enregistre le message fraîchement créé, pour qu'il soit reconnu par le reste du script.
        ScriptNetworkManager.registerMessage(this);
    }

}
