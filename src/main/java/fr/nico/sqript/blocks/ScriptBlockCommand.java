package fr.nico.sqript.blocks;

import fr.nico.sqript.SqriptForge;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.TypeSender;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import fr.nico.sqript.compiling.*;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Block(name = "command",
        description = "Command blocks",
        examples = "command /heal:",
        regex = "^command /.*",
        fields = {
        "side","description","usage","aliases"
        }
)
public class ScriptBlockCommand extends ScriptBlock implements ICommand {

    ScriptParameterDefinition[] argumentsDefinitions;
    private final String name;


    public ScriptBlockCommand(ScriptLine head) {
        final String def = ScriptDecoder.splitAtDoubleDot(head.text.replaceFirst("command\\s+/", ""))[0];
        final String[] args = def.split(" ");
        final List<ScriptParameterDefinition> parameterDefinitions = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            try {
                parameterDefinitions.add(ScriptDecoder.transformPattern(args[i]).getTypes()[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.name = args[0];
        this.argumentsDefinitions = parameterDefinitions.toArray(new ScriptParameterDefinition[0]);
    }


    @Override
    protected void load() throws Exception {

        if (fieldDefined("side"))
            this.setSide(fr.nico.sqript.structures.Side.from(getSubBlock("side").getRawHead()));

        if (!side.isEffectivelyValid())
            return;

        ScriptCompileGroup compileGroup = new ScriptCompileGroup();
        //Adding the "arg" expression to the compile group to prevent false-positive errors
        for (int j = 0; j < argumentsDefinitions.length; j++) {
            compileGroup.add("arg[ument] " + (j + 1));
        }
        compileGroup.add("(sender|player|console|server)", "sender".hashCode());

        this.setRoot(getMainField().compile(compileGroup));

        if (fieldDefined("description"))
            this.setDescription(getSubBlock("description").getRawHead());

        if (fieldDefined("usage"))
            this.setUsage(getSubBlock("usage").getRawHead());

        if (fieldDefined("aliases"))
            this.setAliases(getSubBlock("aliases").getContent().stream().map(s -> s.text).toArray(String[]::new));

        if (side == fr.nico.sqript.structures.Side.BOTH || (side == fr.nico.sqript.structures.Side.CLIENT && side.isEffectivelyValid())) {
            SqriptForge.registerClientCommand(this);
        }

        if (side == fr.nico.sqript.structures.Side.BOTH || (side == fr.nico.sqript.structures.Side.SERVER && side.isEffectivelyValid())){
            SqriptForge.registerServerCommand(this);
        }
    }


    private fr.nico.sqript.structures.Side side = fr.nico.sqript.structures.Side.SERVER;

    private String[] aliases = new String[0];
    private String description;
    private String usage;

    public fr.nico.sqript.structures.Side getSide() {
        return side;
    }

    public void setSide(fr.nico.sqript.structures.Side side) {
        this.side = side;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return usage;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    @SideOnly(Side.CLIENT)
    public void executeOnServer(String[] strings){
        String args = "";
        for(String s: strings)args+=s+" ";
        Minecraft.getMinecraft().getConnection().getNetworkManager().sendPacket(new CPacketChatMessage("/"+getName()+" "+args));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        //System.out.println("executing");

        if((side != fr.nico.sqript.structures.Side.CLIENT && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)){
            executeOnServer(strings);
            if(side == fr.nico.sqript.structures.Side.SERVER)
                return;
        }

        ScriptContext c = new ScriptContext(ScriptManager.GLOBAL_CONTEXT);

        //Adding arguments to the context
        //Arguments can only be numbers, strings or player.
        for (int i = 0; i < argumentsDefinitions.length; i++) {
            Class p = argumentsDefinitions[i].getTypeClass();
            if(i== argumentsDefinitions.length-1 && p== TypeString.class){
                String r = "";
                for(int j = i;j<strings.length;j++){
                    r+=strings[j]+" ";
                }
                c.put(new ScriptAccessor(new TypeString(r),"arg[ument] "+(i+1)));
            }else{
                if(p == TypeString.class)
                {
                    c.put(new ScriptAccessor(new TypeString(strings[i]),"arg[ument] "+(i+1)));
                }
                else if(p == TypeNumber.class)
                {
                    c.put(new ScriptAccessor(new TypeNumber(strings[i]),"arg[ument] "+(i+1)));
                }
                else if(p == TypePlayer.class)
                {
                    c.put(new ScriptAccessor(new TypePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(strings[i])),"arg[ument] "+(i+1)));
                }
            }
        }
        //System.out.println("ICOMMANDSENDER NULL : "+(iCommandSender==null));
        c.put(new ScriptAccessor(new TypeSender(iCommandSender),"(sender|"+(iCommandSender instanceof EntityPlayer?"player":"console|server")+")","sender".hashCode()));

        //Running the associated script
        ScriptClock k = new ScriptClock(c);
        try {
            //System.out.println("Running the command");
            k.start(this);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender iCommandSender) {
        //Going to add a permission system
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings, @Nullable BlockPos blockPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    public ScriptParameterDefinition[] getArgumentsDefinitions() {
        return argumentsDefinitions;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}