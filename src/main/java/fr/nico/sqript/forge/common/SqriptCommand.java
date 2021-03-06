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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
                    if (e instanceof ScriptException.ScriptExceptionList) {
                        sendError("\247cError while reloading the scripts : ",sender);
                        for(Throwable ex : ((ScriptException.ScriptExceptionList) e).exceptionList){
                            sendError("\247c"+ex.getLocalizedMessage()+" (\2478"+ex.getStackTrace()[0]+"\247c)",sender);
                            ex.printStackTrace();
                        }
                    }
                    else{
                        sendError("\247cError while reloading scripts, see stacktrace for more information.",sender);
                        sendError("\247c"+e.getLocalizedMessage(),sender);
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
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r ").appendSibling(new TextComponentString(message)));
    }
    private void sendError(String message, ICommandSender sender){
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r ").appendSibling(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.RED))));
    }

    private void sendHelp(ICommandSender sender) {
        sendMessage("Here are the Sqript commands : ",sender);
        sendMessage("/sqript reload Reloads all the scripts.",sender);
        sendMessage("/sqript generateDoc - Generate a .md file to list all actions, expressions, blocks, and events.",sender);
        sendMessage("/sqript help - Displays this.",sender);
    }
}
