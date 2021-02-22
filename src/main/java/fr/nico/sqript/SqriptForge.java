package fr.nico.sqript;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import fr.nico.sqript.blocks.ScriptBlockCommand;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.network.ScriptSyncDataMessage;
import fr.nico.sqript.meta.*;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(name = "Sqript",modid = "sqript")
public class SqriptForge {

    public static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("sqript");

    public static File scriptDir;

    @Mod.EventHandler
    public static void serverStartEvent(FMLServerStartingEvent event){
        event.registerServerCommand(new SqriptCommand());
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) throws IOException {

        scriptDir = new File("scripts");
        if (!scriptDir.exists()) {
            if (scriptDir.getParentFile() != null) {
                //Dev-environment
                scriptDir = new File(scriptDir.getParentFile().getParentFile(), "scripts");
                if (!scriptDir.exists()) {
                    //first-run of the mod, in production environment
                    scriptDir = new File("scripts");
                    scriptDir.mkdirs();
                }
            } else //First run, in production environment
                scriptDir.mkdirs();
        }
        ScriptManager.preInit(scriptDir);
        List<ModContainer> containerList = new ArrayList<>();

        containerList.add(Loader.instance().activeModContainer());
        ScriptManager.log.info("Loading content of Sqript.");
        //Add dependants to be loaded
        event.getReverseDependencies().forEach((a,b)->{
            containerList.add(Loader.instance().getIndexedModList().get(b));
        });
        try {
            for(ModContainer container : containerList){
                if(container==null)
                    return;
                ScriptManager.log.info("Loading a Sqript Addon : "+container.getName());
                modBuilding(event,container);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScriptManager.init();

    }

    public static void modBuilding(FMLConstructionEvent event, ModContainer container) throws Exception {

        SetMultimap<String, ASMDataTable.ASMData> modData = event.getASMHarvestedData().getAnnotationsFor(container);

        Set<ASMDataTable.ASMData> primitives = modData.get(Primitive.class.getName());
        Set<ASMDataTable.ASMData> types = modData.get(Type.class.getName());
        Set<ASMDataTable.ASMData> events = modData.get(Event.class.getName());
        Set<ASMDataTable.ASMData> expressions = modData.get(Expression.class.getName());
        Set<ASMDataTable.ASMData> actions = modData.get(Action.class.getName());
        Set<ASMDataTable.ASMData> functions = modData.get(Native.class.getName());
        Set<ASMDataTable.ASMData> blocks = modData.get(Block.class.getName());


        for (ASMDataTable.ASMData c : primitives) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Primitive e = (Primitive) toRegister.getAnnotation(Primitive.class);
                    ScriptManager.registerPrimitive(toRegister, e.name(), e.pattern());
                }
            } catch (Exception e) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : types) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Type e = (Type) toRegister.getAnnotation(Type.class);
                    ScriptManager.registerType(toRegister, e.name());
                }
            } catch (Exception e) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }



        for (ASMDataTable.ASMData c : expressions) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Expression e = (Expression) toRegister.getAnnotation(Expression.class);
                    ScriptManager.registerExpression(toRegister, e.name(), e.description(), e.examples(), e.priority(), e.patterns());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptExpression : "+c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }



        for (ASMDataTable.ASMData c : functions) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Native e = (Native) toRegister.getAnnotation(Native.class);
                    ScriptManager.registerNativeFunction(toRegister, e.name(), e.definitions(), e.description(), e.examples());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptFunction : "+c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : events) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Event e = (Event) toRegister.getAnnotation(Event.class);
                    ScriptManager.registerEvent(toRegister, e.name(), e.description(), e.examples(), e.patterns(), e.side(), e.accessors());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptEvent : "+c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : actions) {
            try {
                if(!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder)c.getAnnotationInfo().get("side")).getValue()).isValid()){
                    Class toRegister = Class.forName(c.getClassName());
                    Action e = (Action) toRegister.getAnnotation(Action.class);
                    ScriptManager.registerAction(toRegister, e.name(), e.description(), e.examples(),e.priority(), e.patterns());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptAction : "+c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }
        ScriptManager.actions.sort((a,b)->b.getPriority()-a.getPriority());


        for (ASMDataTable.ASMData c : blocks) {
            try {
                Class toRegister = Class.forName(c.getClassName());
                Block e = (Block) toRegister.getAnnotation(Block.class);
                ScriptManager.registerBlock(toRegister, e.name(), e.description(), e.examples(),e.regex(), e.side());
            } catch (Exception e) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


    }

    public static void registerCommand(ScriptBlockCommand command){
        if(FMLCommonHandler.instance().getSide() == Side.SERVER){
            ScriptManager.log.info("Registering server command : "+command.getName());
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().remove(command.getName());
            ((CommandHandler)FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(command);
        }
        else {
            ScriptManager.log.info("Registering client command : "+command.getName());
            ClientCommandHandler.instance.registerCommand(command);
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        channel.registerMessage(ScriptSyncDataMessage.ScriptSyncDataMessageHandler.class, ScriptSyncDataMessage.class, 0, Side.SERVER);
        channel.registerMessage(ScriptSyncDataMessage.ScriptSyncDataMessageHandler.class, ScriptSyncDataMessage.class, 0, Side.CLIENT);

        channel.registerMessage(ScriptMessage.ScriptMessageHandler.class, ScriptMessage.class, 1, Side.CLIENT);
        channel.registerMessage(ScriptMessage.ScriptMessageHandler.class, ScriptMessage.class, 1, Side.SERVER);

        ScriptNetworkManager.init();

        MinecraftForge.EVENT_BUS.register(new ScriptEventHandler());
    }

}
