package fr.nico.sqript.actions;


import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeMessagePrototype;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Action(name = "Send Actions",
        features = {
            @Feature(name="Send element to console/server", description = "Sends an element to the console or the server, like a packet.",examples = "packet test_packet(message):\n" +
                    "\tclient:\n" +
                    "\t\tprint message\n" +
                    "\tserver:\n" +
                    "\t\tprint message\n" +
                    "\n" +
                    "command /sendPacket {string}:\n" +
                    "\tsend test_packet(arg-1) to server",
                    pattern = "send {element} to (console|server)"),
            @Feature(name="Send element to a player/an element", description = "Sends an element to a player or another element, like a packet or a message.",
                    examples = "packet test_packet(message):\n" +
                            "\tclient:\n" +
                            "\t\tprint message\n" +
                            "\tserver:\n" +
                            "\t\tprint message\n" +
                            "\n" +
                            "command /sendPacket {string}:\n" +
                            "\tsend test_packet(arg-1) to player",
                    pattern = "send {element} to {element}"),
        }
)
public class ActSend extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        //System.out.println("Send paramters : "+ getParameters());
        switch (getMatchedIndex()) {
            case 0:
                ScriptType value = getParameter(1).get(context);
                if(FMLCommonHandler.instance().getSide().isServer())
                    throw new ScriptException.ScriptBadSideException(this.getLine(), FMLCommonHandler.instance().getSide());
                if (value instanceof TypeMessagePrototype) {
                    ScriptMessage msg = (ScriptMessage) value.getObject();
                    SqriptForge.channel.sendToServer(msg);
                }
                break;
            case 1:
                //System.out.println("Value expression : "+getParameter(1).getClass()+ " "+getParameter(1).getMatchedIndex());
                value = getParameter(1).get(context);
                ScriptType dest = getParameter(2).get(context);
                 if (value instanceof TypeMessagePrototype) {
                    ScriptMessage msg = (ScriptMessage) value.getObject();
                    //Parameters has to be a TypeSender
                    if (dest instanceof TypeArray) {
                        for (ScriptType p : ((TypeArray) (dest)).getObject()) {
                            ICommandSender sender = (ICommandSender) p.getObject();
                            if (sender instanceof EntityPlayer) {
                                EntityPlayerMP pl = (EntityPlayerMP) sender;
                                SqriptForge.channel.sendTo(msg, pl);
                            }
                        }
                    } else {
                        ICommandSender sender = (ICommandSender) dest.getObject();
                        if (sender instanceof EntityPlayer) {
                            EntityPlayerMP pl = (EntityPlayerMP) sender;
                            SqriptForge.channel.sendTo(msg, pl);
                        } else if (sender instanceof MinecraftServer) {
                            SqriptForge.channel.sendToServer(msg);
                        }
                    }

                }else  {
                    String msg = value.toString();
                    msg = msg.replaceAll("&","\247");
                    //Parameters has to be a TypeSender
                    if (dest instanceof TypeArray) {
                        for (ScriptType p : ((TypeArray) (dest)).getObject()) {
                            ICommandSender sender = (ICommandSender) p.getObject();
                            synchronized (FMLCommonHandler.instance().getMinecraftServerInstance()) {
                                sender.sendMessage(new TextComponentString(msg));
                            }
                        }
                    } else {
                        ICommandSender sender = (ICommandSender) dest.getObject();
                        sender.sendMessage(new TextComponentString(msg));
                    }

                }
                break;

        }

    }
}
