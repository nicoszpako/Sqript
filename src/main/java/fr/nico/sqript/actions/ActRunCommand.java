package fr.nico.sqript.actions;

import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Action(name = "Run a command",
        features = @Feature(name = "Run a command", description = "Runs a command as the server or as a player", examples = "make console execute command \"time set day\"",pattern = "make {player} execute command {string}")
)
public class ActRunCommand extends ScriptAction {

    @SideOnly(Side.CLIENT)
    public void executeClient(String command){
        if(Minecraft.getMinecraft().getConnection() != null)
            Minecraft.getMinecraft().getConnection().sendPacket((new CPacketChatMessage(command)));
    }

    public void executeServer(ICommandSender sender, String command){
        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(sender,command);
    }

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        System.out.println(getParameter(2,context));
        switch (getMatchedIndex()){
            case 0:
                if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
                    executeClient((String) getParameter(2,context));
                else
                    executeServer((ICommandSender) getParameter(1,context),(String) getParameter(2,context));

        }

    }
}
