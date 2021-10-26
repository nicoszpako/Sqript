package fr.nico.sqript;

import com.google.gson.*;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class SqriptUtils {

    public static double getX(TypeArray array) {
        if (array.getObject().size() > 0)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(0))).getObject();
        else return 0;
    }

    public static double getY(TypeArray array) {
        if (array.getObject().size() > 1)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(1))).getObject();
        else return 0;
    }

    public static double getZ(TypeArray array) {
        if (array.getObject().size() > 2)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(2))).getObject();
        else return 0;
    }

    public static Vec3d arrayToLocation(ArrayList list) {
        if (list.size() == 0)
            return new Vec3d(0, 0, 0);
        if (list.size() == 1)
            return new Vec3d(((TypeNumber) list.get(0)).getObject(), 0, 0);
        if (list.size() == 2)
            return new Vec3d(((TypeNumber) list.get(0)).getObject(), ((TypeNumber) list.get(1)).getObject(), 0);
        else
            return new Vec3d(((TypeNumber) list.get(0)).getObject(), ((TypeNumber) list.get(1)).getObject(), ((TypeNumber) list.get(2)).getObject());
    }

    public static ArrayList locactionToArray(double x, double y, double z) {
        return new ArrayList(Arrays.asList(new TypeNumber(x), new TypeNumber(y), new TypeNumber(z)));
    }

    public static ArrayList locactionToArray(BlockPos pos) {
        return locactionToArray(pos.getX(), pos.getY(), pos.getZ());
    }

    public static ArrayList locactionToArray(EntityPlayer player) {
        return locactionToArray(player.posX, player.posY, player.posZ);
    }

    public static void generateDoc() throws IOException {
        File doc = new File(ScriptManager.scriptDir, "/doc.json");
        //Delete the content of the file
        new PrintWriter(doc).close();

        JsonObject object = new JsonObject();


        JsonObject events = new JsonObject();
        for (EventDefinition eventDefinition : ScriptManager.events) {
                JsonObject event = new JsonObject();
                event.addProperty("name",eventDefinition.getFeature().name());

                JsonObject accessors = new JsonObject();
                for(Feature feature : eventDefinition.getAccessors()){
                    JsonObject accessor = new JsonObject();
                    accessor.addProperty("name", feature.name());
                    accessor.addProperty("description", feature.description());
                    accessor.addProperty("pattern", feature.pattern());
                    accessor.addProperty("type", feature.type());
                    accessor.addProperty("side", feature.side().toString());
                    accessors.add(feature.name(),accessor);
                }
                event.add("accessors", accessors);
                event.addProperty("cancelable",eventDefinition.eventClass.getAnnotation(Cancelable.class) != null);
                event.addProperty("side",eventDefinition.getFeature().side().toString());
                event.addProperty("description", eventDefinition.getFeature().description());
                event.add("examples", toJsonArray(eventDefinition.getFeature().examples()));
                event.addProperty("pattern", eventDefinition.getFeature().pattern());
                events.add(eventDefinition.getFeature().name(), event);
        }
        object.add("events",events );

        JsonObject actions = new JsonObject();
        for (ActionDefinition actionDefinition : ScriptManager.actions) {
            for (Feature feature : actionDefinition.getFeatures()) {
                JsonObject action = new JsonObject();
                action.addProperty("group", actionDefinition.getName());
                action.addProperty("name", feature.name());
                action.addProperty("description", feature.description());
                action.addProperty("pattern", feature.pattern());
                action.addProperty("side", feature.side().toString());
                action.add("examples", toJsonArray(feature.examples()));
                actions.add(feature.name(), action);
            }
        }
        object.add("actions",actions);

        JsonObject expressions = new JsonObject();
        for (ExpressionDefinition expressionDefinition : ScriptManager.expressions) {
            for (Feature feature : expressionDefinition.getFeatures()) {
                JsonObject expression = new JsonObject();
                expression.addProperty("group", expressionDefinition.getName());
                expression.addProperty("name", feature.name());
                expression.addProperty("description", feature.description());
                expression.addProperty("pattern", feature.pattern());
                expression.addProperty("type", feature.type());
                expression.addProperty("side", feature.side().toString());
                expression.add("examples", toJsonArray(feature.examples()));
                expressions.add(feature.name(), expression);
            }
        }
        object.add("expressions",expressions);

        JsonObject blocks = new JsonObject();
        for (BlockDefinition blockDefinition : ScriptManager.blocks) {
            JsonObject block = new JsonObject();
            block.addProperty("name",blockDefinition.getFeature().name());
            block.addProperty("description",blockDefinition.getFeature().description());
            block.addProperty("side",blockDefinition.getFeature().side().toString());
            block.addProperty("reloadable",blockDefinition.isReloadable());
            block.addProperty("regex",blockDefinition.getRegex().pattern());
            block.add("examples",toJsonArray(blockDefinition.getFeature().examples()));
            JsonObject fields = new JsonObject();
            for(Feature feature : blockDefinition.getFields()){
                JsonObject field = new JsonObject();
                field.addProperty("name", feature.name());
                field.addProperty("description", feature.description());
                field.addProperty("pattern", feature.pattern());
                field.addProperty("type", feature.type());
                field.addProperty("side", feature.side().toString());
                fields.add(feature.name(),field);
            }
            block.add("fields", fields);
            blocks.add(blockDefinition.getFeature().name(), block);
        }
        object.add("blocks",blocks);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        PrintWriter printWriter = new PrintWriter(doc);
        printWriter.println(gson.toJson(object));
        printWriter.close();
    }

    public static JsonArray toJsonArray(String[] array){
        JsonArray jsonArray = new JsonArray();
        for(String string: array){
            jsonArray.add(string);
        }
        return jsonArray;
    }

    public static TypeDictionary NBTToDictionary(NBTTagCompound tag){
        TypeDictionary typeDictionary = new TypeDictionary();
        for(String key : tag.getKeySet()){
            switch(tag.getTagId(key)){
                case Constants.NBT.TAG_INT:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getInteger(key)));
                    break;
                case Constants.NBT.TAG_FLOAT:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getFloat(key)));
                    break;
                case Constants.NBT.TAG_DOUBLE:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getDouble(key)));
                    break;
                case Constants.NBT.TAG_LONG:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getLong(key)));
                    break;
                case Constants.NBT.TAG_SHORT:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getShort(key)));
                    break;
                case Constants.NBT.TAG_BYTE:
                    typeDictionary.getObject().put(new TypeString(key), new TypeNumber(tag.getByte(key)));
                    break;
                case Constants.NBT.TAG_BYTE_ARRAY:
                    ArrayList list = new ArrayList();
                    for(byte b : tag.getByteArray(key)){
                        list.add(new TypeNumber(b));
                    }
                    typeDictionary.getObject().put(new TypeString(key), new TypeArray(list));
                    break;
                case Constants.NBT.TAG_LONG_ARRAY:
                case Constants.NBT.TAG_INT_ARRAY:
                    list = new ArrayList();
                    for(int b : tag.getIntArray(key)){
                        list.add(new TypeNumber(b));
                    }
                    typeDictionary.getObject().put(new TypeString(key), new TypeArray(list));
                    break;
                case Constants.NBT.TAG_STRING:
                    typeDictionary.getObject().put(new TypeString(key), new TypeString(tag.getString(key)));
                    break;
                case Constants.NBT.TAG_COMPOUND:
                    typeDictionary.getObject().put(new TypeString(key), NBTToDictionary(tag.getCompoundTag(key)));
                    break;
            }
        }
        return typeDictionary;
    }

    public static <T> T rawInstantiation(Class<?> parent, Class<T> child) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor objDef = parent.getDeclaredConstructor();
        Constructor intConstr = rf.newConstructorForSerialization(child, objDef);
        return child.cast(intConstr.newInstance());
    }

    public static <T> T rawInstantiation(Class<?> parent, Class<T> child, Constructor<?> parentConstructor) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor<?> intConstr = rf.newConstructorForSerialization(child, parentConstructor);
        return child.cast(intConstr.newInstance());
    }

    public static void sendMessage(String message, ICommandSender sender){
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r ").appendSibling(new TextComponentString(message)));
    }

    public static void sendError(String message, ICommandSender sender){
        sender.sendMessage(new TextComponentString("\2478[\2473Sqript\2478]\247r ").appendSibling(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.RED))));
    }

    public static ScriptType<?> getTagFromTypeNBTTagCompound(NBTTagCompound tag, String key){
        switch(tag.getTagId(key)){
            case Constants.NBT.TAG_INT:
                return new TypeNumber(tag.getInteger(key));
            case Constants.NBT.TAG_FLOAT:
                return new TypeNumber(tag.getFloat(key));
            case Constants.NBT.TAG_DOUBLE:
                return new TypeNumber(tag.getDouble(key));
            case Constants.NBT.TAG_LONG:
                return new TypeNumber(tag.getLong(key));
            case Constants.NBT.TAG_SHORT:
                return new TypeNumber(tag.getShort(key));
            case Constants.NBT.TAG_BYTE:
                return new TypeBoolean(tag.getByte(key) == 0 ? false : true);
            case Constants.NBT.TAG_BYTE_ARRAY:
                ArrayList list = new ArrayList();
                for(byte b : tag.getByteArray(key)){
                    list.add(new TypeNumber(b));
                }
                return new TypeArray(list);
            case Constants.NBT.TAG_LONG_ARRAY:
            case Constants.NBT.TAG_INT_ARRAY:
                list = new ArrayList();
                for(int b : tag.getIntArray(key)){
                    list.add(new TypeNumber(b));
                }
                return new TypeArray(list);
            case Constants.NBT.TAG_STRING:
                return new TypeString(tag.getString(key));
            case Constants.NBT.TAG_COMPOUND:
                if(tag.getCompoundTag(key).hasKey("list")){
                    try {
                        TypeArray typeArray = new TypeArray();
                        typeArray.read(tag.getCompoundTag(key));
                        return typeArray;
                    } catch (ScriptException e) {
                        e.printStackTrace();
                        return new TypeNull();
                    }
                } else {
                    return new TypeNBTTagCompound(tag.getCompoundTag(key));
                }
        }
        return new TypeNull();
    }

    public static void setTag(NBTTagCompound tag, String key, ScriptType value){
        if(value instanceof TypeString){
            tag.setString(key, (String) value.getObject());
        } else if(value instanceof TypeBoolean){
            tag.setBoolean(key, (Boolean) value.getObject());
        } else if(value instanceof TypeNumber){
            tag.setDouble(key, (Double) value.getObject());
        } else if(value instanceof TypeArray){
            tag.setTag(key, ((TypeArray) value).write(new NBTTagCompound()));
        }
    }
}
