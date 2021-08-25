package fr.nico.sqript;

import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.blocks.ScriptBlockCommand;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.events.EvtOnScriptLoad;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.blocks.ScriptBlockEvent;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.forge.common.ScriptResourceLoader;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.meta.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptManager {

    public static File scriptDir;

    public static Logger log;

    public static final String version = "1.0";

    //Global context, the variables it contains are saved on shutdown and loaded on start
    public static final ScriptContext GLOBAL_CONTEXT = new ScriptContext();

    //Stores all the scripts
    public static List<ScriptInstance> scripts = new ArrayList<>();

    //Definitions
    public static List<EventDefinition> events = new ArrayList<>();
    public static List<ActionDefinition> actions = new ArrayList<>();
    public static List<BlockDefinition> blocks = new ArrayList<>();
    public static List<ExpressionDefinition> expressions = new ArrayList<>();
    public static List<LoopDefinition> loops = new ArrayList<>();

    public static Map<Class<? extends ScriptElement<?>>, TypeDefinition> types = new HashMap<>();
    public static Map<Class<? extends PrimitiveType<?>>, TypeDefinition> primitives = new HashMap<>();
    public static Map<Class<? extends ScriptNativeFunction>, NativeDefinition> nativeFunctions = new HashMap<>();

    public static List<IScriptParser> parsers = new ArrayList<>();
    
    //Commands
    public static List<ScriptBlockCommand> clientCommands = new ArrayList<>();
    public static List<ScriptBlockCommand> serverCommands = new ArrayList<>();

    //Operations between types
    public static HashMap<ScriptOperator, HashMap<Class, HashMap<Class, IOperation>>> binaryOperations = new HashMap<>();
    public static HashMap<ScriptOperator, HashMap<Class, IOperation>> unaryOperations = new HashMap<>();
    public static List<ScriptOperator> operators = new ArrayList<>();

    public static boolean RELOADING = false;

    public static void registerBinaryOperation(ScriptOperator o, Class a, Class b, IOperation operation) {
        binaryOperations.computeIfAbsent(o, k -> new HashMap<>());
        binaryOperations.get(o).computeIfAbsent(a, k -> new HashMap<>());
        binaryOperations.get(o).get(a).put(b, operation);
    }

    public static void registerUnaryOperation(ScriptOperator o, Class a, IOperation operation) {
        unaryOperations.computeIfAbsent(o, k -> new HashMap<>());
        unaryOperations.get(o).put(a, operation);
    }

    public static IOperation getBinaryOperation(Class<? extends ScriptType> a, Class<? extends ScriptType> b, ScriptOperator o) {
        if (binaryOperations.get(o).get(ScriptType.class) != null) {
            final IOperation op;
            if ((op = binaryOperations.get(o).get(ScriptType.class).get(b)) != null)
                return op;
        }
        if (binaryOperations.get(o).get(a) != null) {
            final IOperation op;
            if ((op = binaryOperations.get(o).get(a).get(ScriptType.class)) != null)
                return op;
        }
        if (binaryOperations.get(o).get(a) != null) {
            final IOperation op;
            if ((op = binaryOperations.get(o).get(a).get(b)) != null)
                return op;
        }
        return null;
    }

    public static ScriptInstance getScriptFromName(String name){
        for(ScriptInstance i : scripts){
            if(i.getName().equals(name))return i;
        }
        return null;
    }

    public static IOperation getUnaryOperation(Class a, ScriptOperator o) {
        try {
            return unaryOperations.get(o).get(a);
        } catch (NullPointerException e) {
            log.error("Operation : '" + o + " is not supported by " + a.getClass().getSimpleName());
        }
        return null;
    }



    public static final boolean FULL_DEBUG = true;

    public static void registerExpression(Class<? extends ScriptExpression> exp, String name, int priority, Feature... features) {
        expressions.add(new ExpressionDefinition(name, exp, priority, features));
        expressions.sort((a,b)->b.getPriority()-a.getPriority());
        log.debug("Registering expression : " + name + " (" + exp.getSimpleName() + ")");
    }

    public static void registerNativeFunction(Class<? extends ScriptNativeFunction> func, String name, String[] definitions, String[] description, String example[]) {
        TransformedPattern[] p = new TransformedPattern[definitions.length];
        Pattern pa = Pattern.compile("(^[\\w]+)");
        for (int i = 0; i < p.length; i++) {
            Matcher m = pa.matcher(definitions[i]);
            if (m.find())
                p[i] = new TransformedPattern(m.group(1));
        }
        nativeFunctions.put(func, new NativeDefinition(name, description, example, func).setTransformedPatterns(p));
        log.debug("Registering native function : " + name + " (" + func.getSimpleName() + ")");
    }

    public static void registerType(Class<? extends ScriptElement<?>> type, String name) throws Exception {
        if (ScriptDecoder.parseType(name) != null)
            throw new Exception("a Type with the name " + name + " already exists !");
        TypeDefinition d = new TypeDefinition(name, new String[0], new String[]{""}, type);
        types.put(type, d);
        log.debug("Registering type : " + name + " (" + type.getSimpleName() + ")");
    }

    public static void registerPrimitive(Class<? extends PrimitiveType<?>> type, String name, String... patterns) {
        log.debug("Registering primitive : " + name + " (" + type.getSimpleName() + ")");
        TypeDefinition primitiveDefinition = new TypeDefinition(name, new String[0], new String[]{""}, type);
        primitiveDefinition.transformedPattern = new TransformedPattern(patterns[0],0,0, new ScriptParameterDefinition[][]{{new ScriptParameterDefinition(type,false)}});
        primitives.put(type, primitiveDefinition);
    }


    public static void registerLoop(Class<? extends ScriptEvent> cls, String name, String pattern, Side side, int priority) {
        log.debug("Registering loop : " + name + " (" + cls.getSimpleName() + ")");
        loops.add(new LoopDefinition(pattern,cls,side,name,priority));
        loops.sort((a,b)->b.getPriority()-a.getPriority());

    }

    public static void registerEvent(Class<? extends ScriptEvent> cls, Feature feature, Feature[] accesors) {
        log.debug("Registering event : " + feature.name() + " (" + cls.getSimpleName() + ")");
        events.add(new EventDefinition(cls, feature, accesors));
    }

    public static void registerAction(Class<? extends ScriptAction> cls, String name, int priority, Feature... features) {
        log.debug("Registering action : " + name + " (" + cls.getSimpleName() + ")");
        actions.add(new ActionDefinition(name, cls, priority, features));
    }

    public static void registerBlock(Class<? extends ScriptBlock> cls, Feature feature, Feature[] fields,boolean reloadable) {
        log.debug("Registering block : " + feature.name() + " (" + cls.getSimpleName() + ")");
        blocks.add(new BlockDefinition(cls, feature, fields, reloadable));
    }



    public static void registerOperator(ScriptOperator operator) {
        for (ScriptOperator o : operators) {
            if (o.symbol.equals(operator.symbol) && operator.unary == o.unary) {
                log.error("Cannot register the operator " + operator + " as a" + (o.unary ? "n unary " : " binary ") + "operator with the symbol '" + operator.symbol + "' already exists.");
                return;
            }
        }
        //SqriptMod.log.info("Registering operator : "+operator.symbol);
        operators.add(operator);
    }

    public static void preInit(File scriptsFolder){
        log = LogManager.getLogger("Sqript");
        log.info("Sqript" + " version " + ScriptManager.version + " is running, by Nico-");
        scriptDir = scriptsFolder;

    }

    public static void init() {


        try {
            ScriptDataManager.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initOperators();
        buildOperators();
        ScriptDecoder.init();

        try {
            ScriptManager.log.info("Loading scripts.");
            loadScripts(scriptDir);
        }catch (Throwable e) {
            ScriptManager.log.error(e.getMessage());
        }


        SqriptForge.registerCommands();

    }




    public static void stop() {
        try {
            ScriptDataManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Built-in operators
    private static void initOperators() {
        registerOperator(ScriptOperator.PLUS_UNARY);
        registerOperator(ScriptOperator.MINUS_UNARY);
        registerOperator(ScriptOperator.MULTIPLY);
        registerOperator(ScriptOperator.MOD);
        registerOperator(ScriptOperator.QUOTIENT);
        registerOperator(ScriptOperator.DIVIDE);
        registerOperator(ScriptOperator.ADD);
        registerOperator(ScriptOperator.SUBTRACT);
        registerOperator(ScriptOperator.MTE);
        registerOperator(ScriptOperator.LTE);
        registerOperator(ScriptOperator.LT);
        registerOperator(ScriptOperator.MT);
        registerOperator(ScriptOperator.NOT_EQUAL);
        registerOperator(ScriptOperator.EQUAL);
        registerOperator(ScriptOperator.NOT);
        registerOperator(ScriptOperator.AND);
        registerOperator(ScriptOperator.EXP);
        registerOperator(ScriptOperator.OR);
        registerOperator(ScriptOperator.FACTORIAL);

    }



    private static void buildOperators() {
        for (ScriptOperator s : operators) {
            if(s.word && !s.unary){
                ScriptDecoder.operators_list.add("(\\)\\s+|^)" + Pattern.quote(s.symbol) + "(\\s+\\()");
            }else if (s.word) {
                ScriptDecoder.operators_list.add("(\\s+|^)" + Pattern.quote(s.symbol) + "(\\s+\\()");
            }else{
                ScriptDecoder.operators_list.add(Pattern.quote(s.symbol));
            }
            ScriptDecoder.operators_pattern.add(Pattern.compile(ScriptDecoder.operators_list.get(ScriptDecoder.operators_list.size()-1)));
        }
    }

    public static void handleError(ScriptToken line, Throwable throwable){
        ScriptManager.log.error("Error while loading " + line.getScriptInstance().getName() + " : ");
        if (throwable instanceof ScriptException) {
            for (String s : throwable.getMessage().split("\n"))
                ScriptManager.log.error(s);
        }
        if (ScriptManager.FULL_DEBUG)
            throwable.printStackTrace();
    }

    private static void loadFolder(File folder) throws Throwable {
        ScriptException.ScriptExceptionList list = new ScriptException.ScriptExceptionList();
        for(File f : folder.listFiles()) {
            if(f.isDirectory()){
                loadFolder(f);
                if(FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
                    ScriptManager.loadResources(f);
            }
            else if (f.toPath().toString().endsWith(".sq")) {
                ScriptLoader loader = new ScriptLoader(f);
                try{
                    ScriptInstance instance = loader.loadScript();
                    instance.callEvent(new ScriptContext(GLOBAL_CONTEXT),new EvtOnScriptLoad(f));
                    scripts.add(instance);
                }catch(Exception e){
                    if(e instanceof ScriptException.ScriptExceptionList) {
                        list.exceptionList.addAll(((ScriptException.ScriptExceptionList) (e)).exceptionList);
                    }else
                        throw e;
                }

            }
        }
        if(!list.exceptionList.isEmpty()){
            throw list;
        }
    }

    public static void loadScripts(File mainFolder) throws Throwable {

        loadFolder(mainFolder);

        if(FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
            loadResources(mainFolder);

        if(FULL_DEBUG)
            for(ScriptInstance instance : scripts){
                List<ScriptBlock> list =  instance.getBlocksOfClass(ScriptBlockEvent.class);
                list.addAll(instance.getBlocksOfClass(ScriptBlockCommand.class));
                //System.out.println("# Of blocks : "+instance.getBlocks().size());
                for (ScriptBlock s : list)  {
                    //System.out.println("Displaying : "+s.getHead());
                    ScriptLoader.dispScriptTree(s, 0);
                }
                log.info("");
            }
        log.info("All scripts are loaded");
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static void loadResources(File mainFolder) throws IllegalAccessException {
        //System.out.println("Adding "+mainFolder+" to loaded resources.");
        ScriptResourceLoader resourceLoader = new ScriptResourceLoader();
        Field defaultResourcePacksField = ObfuscationReflectionHelper.findField(Minecraft.getMinecraft().getClass(),"field_110449_ao");
        defaultResourcePacksField.setAccessible(true);
        ((ArrayList)defaultResourcePacksField.get(Minecraft.getMinecraft())).add(resourceLoader);
    }


    //Runs the events triggers and returns the final context (that has passed through all the triggers)
    public static ScriptContext callEventAndGetContext(ScriptEvent event) throws ScriptException {
        ScriptContext context = new ScriptContext(GLOBAL_CONTEXT);
        context.setReturnValue(new ScriptTypeAccessor(TypeBoolean.FALSE(), ""));
        for(ScriptInstance instance : scripts) {
            context = instance.callEventAndGetContext(context,event);
        }
        return context;
    }


    //True if the event has been cancelled
    public static boolean callEvent(ScriptEvent event) {
        Optional<EventDefinition> optional = ScriptManager.events.stream().filter(a->a.eventClass == event.getClass()).findFirst();
        EventDefinition eventDefinition = null;
        if(optional.isPresent())
            eventDefinition = optional.get();
        if( eventDefinition!=null && eventDefinition.getFeature().side().isStrictlyValid()) {
            ScriptContext context = new ScriptContext(GLOBAL_CONTEXT);
            if(RELOADING)
                return false;
            for (ScriptInstance script : scripts) {
                if (RELOADING)
                    return false;
                if (script.callEvent(context, event)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static void clearClientCommands(){
        for(ScriptBlockCommand command : clientCommands){
            ClientCommandHandler.instance.getCommands().remove(command.getName());
        }
        clientCommands.clear();
    }

    public static void clearServerCommands(){
        for(ScriptBlockCommand command : serverCommands){
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().remove(command.getName());
        }
        serverCommands.clear();
    }

    public static void reload() throws Throwable {
        RELOADING = true;

        scripts.clear();

        if(FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
            clearClientCommands();
        if(FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.SERVER)
            clearServerCommands();

        ScriptTimer.reload();
        loadScripts(scriptDir);
        SqriptForge.registerCommands();

        RELOADING = false;
    }
}
