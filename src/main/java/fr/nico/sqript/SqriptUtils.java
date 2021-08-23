package fr.nico.sqript;

import com.google.gson.*;
import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import scala.actors.migration.pattern;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
            block.addProperty("name",blockDefinition.getName());
            block.addProperty("description",blockDefinition.getDescription());
            block.addProperty("side",blockDefinition.getSide().toString());
            block.addProperty("reloadable",blockDefinition.isReloadable());
            block.addProperty("regex",blockDefinition.getRegex().pattern());
            block.add("example",toJsonArray(blockDefinition.getExample()));
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

    public static <T> T rawInstantiation(Class<?> parent, Class<T> child) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor objDef = parent.getDeclaredConstructor();
        Constructor intConstr = rf.newConstructorForSerialization(child, objDef);
        return child.cast(intConstr.newInstance());
    }
}
