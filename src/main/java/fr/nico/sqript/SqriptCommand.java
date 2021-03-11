package fr.nico.sqript;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class SqriptCommand extends CommandBase {

    @Override
    public String getName() {
        return "sqript";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Run /sqript help to get help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>0){
            if(args[0].equalsIgnoreCase("reload")){
                long t = System.currentTimeMillis();
                sendMessage("Reloading all scripts",sender);
                try{
                    ScriptManager.reload();
                }catch(Throwable e){
                    if (e instanceof ScriptException) {
                        sendMessage("\247cError while loading " + (((ScriptException)e).getLine().getScriptInstance().getName()) + " : ",sender);
                        for (String s : e.getMessage().split("\n"))
                            sendMessage("\247c"+s,sender);
                    }
                    else{
                        sendMessage("\247cError while reloading scripts, see stacktrace for more information.",sender);
                        sendMessage(e.getMessage(),sender);
                    }
                }
                sendMessage("Done in "+((System.currentTimeMillis()-t)/1000d)+" seconds",sender);
            }
            if(args[0].equalsIgnoreCase("generateDoc")){
                try {
                    SqriptUtils.generateDoc();
                    sendMessage("Generated documentation at scripts/doc.md",sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            sendHelp(sender);
        }
    }

    private void sendMessage(String message,ICommandSender sender){
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r "+message));
    }

    private void sendHelp(ICommandSender sender) {

    }
}
