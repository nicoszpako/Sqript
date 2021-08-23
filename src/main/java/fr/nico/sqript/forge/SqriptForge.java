package fr.nico.sqript.forge;

import com.google.common.collect.SetMultimap;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlockCommand;
import fr.nico.sqript.events.EvtFML;
import fr.nico.sqript.events.EvtOnWindowSetup;
import fr.nico.sqript.forge.common.ScriptBlock.ScriptBlock;
import fr.nico.sqript.forge.common.ScriptEventHandler;
import fr.nico.sqript.forge.common.SqriptCommand;
import fr.nico.sqript.forge.common.item.ScriptItem;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.network.ScriptSyncDataMessage;
import fr.nico.sqript.meta.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.Item;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Mod(name = "Sqript", modid = "sqript")
@Mod.EventBusSubscriber(modid = "sqript")
public class SqriptForge {

    public static String MODID = "sqript";

    public static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("sqript");

    public static File scriptDir;

    public static ArrayList<ScriptBlock> blocks = new ArrayList<>();


    public static ArrayList<ScriptItem> scriptItems = new ArrayList<>();
    public static ArrayList<Item> items = new ArrayList<>();

    @Mod.EventHandler
    public static void serverStartEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(new SqriptCommand());
        for (ScriptBlockCommand command : ScriptManager.serverCommands) {
            ScriptManager.log.info("Registering server command : " + command.getName());
            event.getServer().getCommandManager().getCommands().remove(command.getName());
            event.registerServerCommand(command);
        }
        ScriptManager.callEvent(new EvtFML.EvtOnServerStartingEvent(event.getServer()));
    }

    @Mod.EventHandler
    public static void serverStopEvent(FMLServerStoppingEvent event) {
        ScriptManager.stop();
    }

    @Mod.EventHandler
    public void
    construction(FMLConstructionEvent event) throws IOException {

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
        ScriptManager.log.info("Loading content of Sqript.");
        //Add dependants to be loaded
        ModContainer sqriptContainer = Loader.instance().activeModContainer();
        try {
            modBuilding(event, sqriptContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ModContainer> containerList = new ArrayList<>(Loader.instance().getIndexedModList().values());
        containerList.remove(sqriptContainer);
        for (ModContainer container : containerList) {
            try {
                if (container == null || container.getName().equalsIgnoreCase("Minecraft") || container.getName().equalsIgnoreCase("Minecraft"))
                    continue;
                modBuilding(event, container);
            } catch (Exception e) {
                if (!(e instanceof NullPointerException)) {
                    ScriptManager.log.error("Error while loading Sqript addon : " + container.getName());
                    e.printStackTrace();
                }
            }
        }

        ScriptManager.init();

        ScriptManager.callEvent(new EvtOnWindowSetup());

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
        Set<ASMDataTable.ASMData> loops = modData.get(Loop.class.getName());


        for (ASMDataTable.ASMData c : primitives) {
            try {
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
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
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
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
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Expression e = (Expression) toRegister.getAnnotation(Expression.class);
                    ScriptManager.registerExpression(toRegister, e.name(), e.priority(), e.features());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptExpression : " + c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : loops) {
            try {
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Loop e = (Loop) toRegister.getAnnotation(Loop.class);
                    ScriptManager.registerLoop(toRegister, e.name(), e.pattern(), e.side(), e.priority());
                }
            } catch (Exception e) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : functions) {
            try {
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Native e = (Native) toRegister.getAnnotation(Native.class);
                    ScriptManager.registerNativeFunction(toRegister, e.name(), e.definitions(), e.description(), e.examples());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptFunction : " + c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : events) {
            try {
                Class toRegister = Class.forName(c.getClassName());
                Event e = (Event) toRegister.getAnnotation(Event.class);
                ScriptManager.registerEvent(toRegister, e.feature(), e.accessors());
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptEvent : " + c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


        for (ASMDataTable.ASMData c : actions) {
            try {
                if (!c.getAnnotationInfo().containsKey("side") || fr.nico.sqript.structures.Side.from(((ModAnnotation.EnumHolder) c.getAnnotationInfo().get("side")).getValue()).isStrictlyValid()) {
                    Class toRegister = Class.forName(c.getClassName());
                    Action e = (Action) toRegister.getAnnotation(Action.class);
                    ScriptManager.registerAction(toRegister, e.name(), e.priority(), e.features());
                }
            } catch (Exception e) {
                ScriptManager.log.error("Error trying to load ScriptAction : " + c.getClassName());
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }
        ScriptManager.actions.sort((a, b) -> b.getPriority() - a.getPriority());


        for (ASMDataTable.ASMData c : blocks) {
            try {
                Class toRegister = Class.forName(c.getClassName());
                Block e = (Block) toRegister.getAnnotation(Block.class);
                ScriptManager.registerBlock(toRegister, e.name(), e.description(), e.examples(), e.regex(), e.side(), e.reloadable());
            } catch (Exception e) {
                if (ScriptManager.FULL_DEBUG) e.printStackTrace();
            }
        }


    }

    public static void registerClientCommand(ScriptBlockCommand command) {
        ScriptManager.log.info("Registering client command : " + command.getName());
        ClientCommandHandler.instance.registerCommand(command);
    }

    @SideOnly(Side.CLIENT)
    public static void addClientCommand(ScriptBlockCommand command) {
        ScriptManager.clientCommands.add(command);
        registerClientCommand(command);

    }

    public static void addServerCommand(ScriptBlockCommand command) {
        ScriptManager.serverCommands.add(command);

    }

    public static void registerCommands() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            for (ScriptBlockCommand command : ScriptManager.clientCommands) {
                ScriptManager.log.info("Registering client command : " + command.getName());
                ClientCommandHandler.instance.registerCommand(command);
            }
        } else {
            for (ScriptBlockCommand command : ScriptManager.serverCommands) {
                ScriptManager.log.info("Registering server command : " + command.getName());
                Objects.requireNonNull(FMLCommonHandler.instance().getMinecraftServerInstance().getServer()).getCommandManager().getCommands().put(command.getName(), command);
                Objects.requireNonNull(FMLCommonHandler.instance().getMinecraftServerInstance().getServer()).getCommandManager().getCommands().put(command.getName(), command);
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        channel.registerMessage(ScriptSyncDataMessage.ScriptSyncDataMessageHandler.class, ScriptSyncDataMessage.class, 0, Side.SERVER);
        channel.registerMessage(ScriptSyncDataMessage.ScriptSyncDataMessageHandler.class, ScriptSyncDataMessage.class, 0, Side.CLIENT);

        channel.registerMessage(ScriptMessage.ScriptMessageHandler.class, ScriptMessage.class, 1, Side.CLIENT);
        channel.registerMessage(ScriptMessage.ScriptMessageHandler.class, ScriptMessage.class, 1, Side.SERVER);

        ScriptNetworkManager.init();

        MinecraftForge.EVENT_BUS.register(new ScriptEventHandler());
        ScriptManager.callEvent(new EvtFML.EvtOnPreInit());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        ScriptManager.callEvent(new EvtFML.EvtOnInit());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        ScriptManager.callEvent(new EvtFML.EvtOnPostInit());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        //System.out.println("Registering Sqript items, there are : "+ scriptItems.size()+" item(s) to register.");
        for (ScriptItem e : scriptItems) {
            //System.out.println("Registering : "+e.getItem().getRegistryName());
            event.getRegistry().register(e.getItem());
        }
        for (Item e : items) {
            //System.out.println("Registering : "+e.getRegistryName());
            event.getRegistry().register(e);
        }

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<net.minecraft.block.Block> event) {
        for (net.minecraft.block.Block e : blocks) {
            //System.out.println("Registering : "+e.getRegistryName());
            event.getRegistry().register(e);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (ScriptItem e : scriptItems) {
            if (e.customModel.isEmpty())
                ModelLoader.setCustomModelResourceLocation(e.getItem(), 0, new ModelResourceLocation(
                        e.getItem().getRegistryName(), "inventory"));
            else
                ModelLoader.setCustomModelResourceLocation(e.getItem(), 0, new ModelResourceLocation(
                        e.customModel, "inventory"));
        }
        for (ScriptBlock e : blocks) {
            ModelLoader.setCustomStateMapper(e, new DefaultStateMapper());
        }
        for (Item e : items) {
            ModelLoader.setCustomModelResourceLocation(e, 0, new ModelResourceLocation(
                    e.getRegistryName(), "inventory"));
        }

    }

}
