package fr.nico.sqript;

import com.google.gson.*;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.*;
import fr.nico.sqript.compiling.*;
import fr.nico.sqript.events.EvtOnScriptLoad;
import fr.nico.sqript.events.ScriptEvent;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.forge.common.ScriptResourceLoader;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.function.ScriptNativeFunction;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.PrimitiveType;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public static HashMap<ScriptOperator, HashMap<Class, HashMap<Class, OperatorDefinition>>> binaryOperations = new HashMap<>();
    public static HashMap<ScriptOperator, HashMap<Class, OperatorDefinition>> unaryOperations = new HashMap<>();
    public static List<ScriptOperator> operators = new ArrayList<>();

    //Type parsers
    public static HashMap<Class, HashMap<Class, TypeParserDefinition>> typeParsers = new HashMap<>();

    public static boolean RELOADING = false;

    public static void registerBinaryOperation(ScriptOperator o, Class a, Class b, Class<? extends ScriptElement<?>> returnType, IOperation operation) {
        registerBinaryOperation(o, a, b, returnType, operation, 0);
    }

    public static void registerBinaryOperation(ScriptOperator o, Class a, Class b, Class<? extends ScriptElement<?>> returnType, IOperation operation, int priority) {
        binaryOperations.computeIfAbsent(o, k -> new HashMap<>());
        binaryOperations.get(o).computeIfAbsent(a, k -> new HashMap<>());
        binaryOperations.get(o).get(a).put(b, new OperatorDefinition(operation, returnType, priority));
    }

    public static void registerTypeParser(Class from, Class to, ITypeParser parser, int priority) {
        typeParsers.computeIfAbsent(from, k -> new HashMap<>());
        typeParsers.get(from).put(to, new TypeParserDefinition(priority, parser, from, to));
    }

    public static void registerUnaryOperation(ScriptOperator o, Class<? extends ScriptElement<?>> a, Class<? extends ScriptElement<?>> returnType, IOperation operation) {
        unaryOperations.computeIfAbsent(o, k -> new HashMap<>());
        unaryOperations.get(o).put(a, new OperatorDefinition(operation, returnType, 0));
    }

    public static TypeParserDefinition getParser(Class c1, Class c2){
        TypeParserDefinition a = null;
        TypeParserDefinition b = null;
        if(ScriptManager.typeParsers.get(c1)!=null)
            a = ScriptManager.typeParsers.get(c1).get(c2);
        if(ScriptManager.typeParsers.get(c2)!=null)
            b = ScriptManager.typeParsers.get(c2).get(c1);
        if (b == null && a == null) {
            return null;
        }
        if(b != null && a != null){
            if(b.getPriority()>a.getPriority())
                return b;
            else return a;
        }
        if(b == null)
            return a;
        else
            return b;
    }

    public static ScriptType parse(ScriptType from, Class toType) {
        if (toType == TypeString.class)
            return new TypeString(from.toString());
        else {
            TypeParserDefinition typeParserDefinition = getParser(from.getClass(),toType);
            if (typeParserDefinition != null)
                return typeParserDefinition.getParser().parse(from);
            else return null;
        }
    }

    public static OperatorDefinition getBinaryOperation(Class<? extends ScriptType> a, Class<? extends ScriptType> b, ScriptOperator o) {
        //System.out.println("Getting binary operation for : "+o+" from "+a+" to "+b);
        OperatorDefinition op = null;
        if (binaryOperations.get(o) != null) {
            if (binaryOperations.get(o).get(ScriptElement.class) != null) {
                final OperatorDefinition def = binaryOperations.get(o).get(ScriptElement.class).get(b);
                if (def != null)
                    op = binaryOperations.get(o).get(a).get(b);
                //System.out.println("3)"+op+" for "+a +" "+b);
            }
            if (binaryOperations.get(o).get(a) != null) {
                op = binaryOperations.get(o).get(a).get(ScriptElement.class);
                //System.out.println("1)"+op+" for "+a +" "+b);
            }
            if (binaryOperations.get(o).get(a) != null) {
                final OperatorDefinition def = binaryOperations.get(o).get(a).get(b);
                if (def != null && (op == null || def.getPriority() > op.getPriority()))
                    op = binaryOperations.get(o).get(a).get(b);
                //System.out.println("2)"+op+" for "+a +" "+b);
            }
            TypeParserDefinition typeParserDefinition = getParser(a,b);
            if(typeParserDefinition != null)
                if (binaryOperations.get(o).get(typeParserDefinition.getFrom()) != null) {
                    if((op = binaryOperations.get(o).get(typeParserDefinition.getFrom()).get(typeParserDefinition.getTo())) != null){
                        return op;
                    }
                }
        }
        return op;
    }

    public static ScriptInstance getScriptFromName(String name) {
        for (ScriptInstance i : scripts) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    public static OperatorDefinition getUnaryOperation(Class a, ScriptOperator o) {
        try {
            return unaryOperations.get(o).get(a);
        } catch (NullPointerException e) {
            log.error("Operation : '" + o + " is not supported by " + a.getClass().getSimpleName());
        }
        return null;
    }


    public static ActionDefinition getDefinitionFromAction(Class<? extends ScriptAction> cls) {
        for (ActionDefinition actionDefinition : actions) {
            if (actionDefinition.getActionClass() == cls)
                return actionDefinition;
        }
        return null;
    }

    public static ExpressionDefinition getDefinitionFromExpression(Class<? extends ScriptExpression> cls) {
        for (ExpressionDefinition expressionDefinition : expressions) {
            if (expressionDefinition.getExpressionClass() == cls)
                return expressionDefinition;
        }
        return null;
    }

    public static final boolean FULL_DEBUG = true;

    public static void registerExpression(Class<? extends ScriptExpression> exp, String name, int priority, Feature... features) throws Exception {
        expressions.add(new ExpressionDefinition(name, exp, priority, features));
        expressions.sort((a, b) -> b.getPriority() - a.getPriority());
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
        primitiveDefinition.transformedPattern = new TransformedPattern(patterns[0], 0, 0, new ScriptParameterDefinition[][]{{new ScriptParameterDefinition(type, false)}});
        primitives.put(type, primitiveDefinition);
    }


    public static void registerLoop(Class<? extends ScriptEvent> cls, String name, String pattern, Side side, int priority) {
        log.debug("Registering loop : " + name + " (" + cls.getSimpleName() + ")");
        loops.add(new LoopDefinition(pattern, cls, side, name, priority));
        loops.sort((a, b) -> b.getPriority() - a.getPriority());

    }

    public static void registerEvent(Class<? extends ScriptEvent> cls, Feature feature, Feature[] accesors) throws Exception {
        log.debug("Registering event : " + feature.name() + " (" + cls.getSimpleName() + ")");
        events.add(new EventDefinition(cls, feature, accesors));
    }

    public static void registerAction(Class<? extends ScriptAction> cls, String name, int priority, Feature... features) throws Exception {
        log.debug("Registering action : " + name + " (" + cls.getSimpleName() + ")");
        actions.add(new ActionDefinition(name, cls, priority, features));
    }

    public static void registerBlock(Class<? extends ScriptBlock> cls, Feature feature, Feature[] fields, boolean reloadable) {
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

    public static void preInit(File scriptsFolder) {
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
        } catch (Throwable e) {
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
            if (s.word && !s.unary) {
                ScriptDecoder.operators_list.add("(\\)\\s+|^)" + Pattern.quote(s.symbol) + "(\\s+\\()");
            } else if (s.word) {
                ScriptDecoder.operators_list.add("(\\s+|^)" + Pattern.quote(s.symbol) + "(\\s+\\()");
            } else {
                ScriptDecoder.operators_list.add(Pattern.quote(s.symbol));
            }
            ScriptDecoder.operators_pattern.add(Pattern.compile(ScriptDecoder.operators_list.get(ScriptDecoder.operators_list.size() - 1)));
        }
    }

    public static void handleError(ScriptToken line, Throwable throwable) {
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
        if (FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
            ScriptManager.loadResources();
        for (File f : folder.listFiles()) {
            //System.out.println("Looping : "+f.getName()+" in : "+ Arrays.toString(folder.listFiles()));
            if (f.isDirectory()) {
                try {
                    loadFolder(f);
                } catch (Exception e) {
                    if (e instanceof ScriptException.ScriptExceptionList) {
                        list.exceptionList.addAll(((ScriptException.ScriptExceptionList) (e)).exceptionList);
                    } else {
                        //System.out.println("Throwing exception.");
                        throw e;
                    }
                }
                if (f.getName().equalsIgnoreCase("sounds")) {
                    for (File file : f.listFiles()) {
                        log.info("Registering sound : " + folder.getName() + ":" + file.getName().split("\\.")[0]);
                        addSoundToJsonFile(new File(folder, "sounds.json"), folder.getName(), file.getName().split("\\.")[0]);
                        SoundEvent event = new SoundEvent(new ResourceLocation(folder.getName() + ":" + file.getName().split("\\.")[0]));
                        event.setRegistryName(folder.getName() + ":" + file.getName().split("\\.")[0]);
                        SqriptForge.soundEvents.add(event);
                    }
                }
            } else if (f.toPath().toString().endsWith(".sq")) {
                ScriptLoader loader = new ScriptLoader(f);
                try {
                    ScriptInstance instance = loader.loadScript();
                    instance.callEvent(new ScriptContext(GLOBAL_CONTEXT), new EvtOnScriptLoad(f));
                    scripts.add(instance);
                } catch (Exception e) {
                    if (e instanceof ScriptException.ScriptExceptionList) {
                        list.exceptionList.addAll(((ScriptException.ScriptExceptionList) (e)).exceptionList);
                    } else {
                        //System.out.println("Throwing exception.");
                        throw e;
                    }
                }

            }
        }
        if (!list.exceptionList.isEmpty()) {
            throw list;
        }
    }

    private static void addSoundToJsonFile(File file, String domain, String name) throws IOException {
        if (!file.exists())
            file.createNewFile();

        //Parsing file and adding new json object
        JsonParser parser = new JsonParser();
        JsonObject obj = new JsonObject();

        try {
            obj = parser.parse(Files.newBufferedReader(file.toPath())).getAsJsonObject();
        } catch (Exception ignored) {
        }

        JsonObject newSound = new JsonObject();
        newSound.add("category", new JsonPrimitive("master"));
        newSound.add("subtitle", new JsonPrimitive("subtitle." + name));
        JsonArray sounds = new JsonArray();
        sounds.add(domain + ":" + name);
        newSound.add("sounds", sounds);
        obj.add(name, newSound);

        //Creating as instance of gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Writing the new content to the file
        new PrintWriter(file).close();
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.println(gson.toJson(obj));
        printWriter.close();
    }

    public static void loadScripts(File mainFolder) throws Throwable {
        ScriptException.ScriptExceptionList elist = new ScriptException.ScriptExceptionList();
        try{
            loadFolder(mainFolder);
        }catch(ScriptException.ScriptExceptionList e){
            elist = e;
        }

        if (FMLCommonHandler.instance().getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
            loadResources();

        if (FULL_DEBUG && !RELOADING)
            for (ScriptInstance instance : scripts) {
                List<ScriptBlock> list = instance.getBlocksOfClass(ScriptBlockEvent.class);
                list.addAll(instance.getBlocksOfClass(ScriptBlockCommand.class));
                list.addAll(instance.getBlocksOfClass(ScriptBlockPacket.class));
                list.addAll(instance.getBlocksOfClass(ScriptBlockTimeLoop.class));
                list.addAll(instance.getBlocksOfClass(ScriptBlockFunction.class));
                //System.out.println("# Of blocks : "+instance.getBlocks().size());
                for (ScriptBlock s : list) {
                    //System.out.println("Displaying : "+s.getHead());
                    ScriptLoader.dispScriptTree(s, 0);
                }
                log.info("");
            }
        log.info("All scripts are loaded");
        if(!elist.exceptionList.isEmpty())
            throw elist;
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static void loadResources() throws IllegalAccessException {
        //System.out.println("Adding "+mainFolder+" to loaded resources.");
        ScriptResourceLoader resourceLoader = new ScriptResourceLoader();
        Field defaultResourcePacksField = ObfuscationReflectionHelper.findField(Minecraft.getMinecraft().getClass(), "field_110449_ao");
        defaultResourcePacksField.setAccessible(true);
        ((ArrayList) defaultResourcePacksField.get(Minecraft.getMinecraft())).add(resourceLoader);
    }


    //Runs the events triggers and returns the final context (that has passed through all the triggers)
    public static ScriptContext callEventAndGetContext(ScriptEvent event) throws ScriptException {
        ScriptContext context = new ScriptContext(GLOBAL_CONTEXT);
        context.setReturnValue(new ScriptTypeAccessor(TypeBoolean.FALSE(), ""));
        for (ScriptInstance instance : scripts) {
            context = instance.callEventAndGetContext(context, event);
        }
        return context;
    }


    //True if the event has been cancelled
    public static boolean callEvent(ScriptEvent event) {
        Optional<EventDefinition> optional = ScriptManager.events.stream().filter(a -> a.eventClass == event.getClass()).findFirst();
        EventDefinition eventDefinition = null;
        if (optional.isPresent())
            eventDefinition = optional.get();
        boolean result = false;
        if (eventDefinition != null && eventDefinition.getFeature().side().isStrictlyValid()) {
            ScriptContext context = new ScriptContext(GLOBAL_CONTEXT);
            if (RELOADING)
                return false;
            for (ScriptInstance script : scripts) {
                if (RELOADING)
                    return false;
                if (script.callEvent(context, event)) {
                    //System.out.println("Returning true");
                    result = true;
                }
            }
        }
        return result;
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static void clearClientCommands() {
        for (ScriptBlockCommand command : clientCommands) {
            ClientCommandHandler.instance.getCommands().remove(command.getName());
        }
        clientCommands.clear();
    }

    public static void clearServerCommands() {
        for (ScriptBlockCommand command : serverCommands) {
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().remove(command.getName());
        }
        serverCommands.clear();
    }

    public static void reload() throws Throwable {
        RELOADING = true;

        scripts.clear();
        ScriptNetworkManager.clear();

        if (FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.CLIENT)
            clearClientCommands();
        if (FMLCommonHandler.instance().getEffectiveSide() == net.minecraftforge.fml.relauncher.Side.SERVER)
            clearServerCommands();

        ScriptTimer.reload();
        ScriptException.ScriptExceptionList elist = new ScriptException.ScriptExceptionList();
        try{
            loadScripts(scriptDir);
        }catch(ScriptException.ScriptExceptionList e){
            elist = e;
        }

        SqriptForge.registerCommands();

        RELOADING = false;

        if(!elist.exceptionList.isEmpty())
            throw elist;
    }
}
