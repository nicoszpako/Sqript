package fr.nico.sqript.forge.common;

import com.google.common.collect.Lists;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

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
                        for (String s : e.getLocalizedMessage().split("\n"))
                            sendMessage("\247c"+s,sender);
                    }
                    else{
                        sendMessage("\247cError while reloading scripts, see stacktrace for more information.",sender);
                        sendMessage("\247c"+e.getLocalizedMessage(),sender);
                        e.printStackTrace();
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

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length==1){
            return getListOfStringsMatchingLastWord(args,Lists.newArrayList("reload","help","generateDoc"));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    private void sendMessage(String message, ICommandSender sender){
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r "+message));
    }

    private void sendHelp(ICommandSender sender) {
        sendMessage("Here are the Sqript commands : ",sender);
        sendMessage("/sqript reload Reloads all the scripts.",sender);
        sendMessage("/sqript generateDoc - Generate a .md file to list all actions, expressions, blocks, and events.",sender);
        sendMessage("/sqript help - Displays this.",sender);
    }
}
