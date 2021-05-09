package fr.nico.sqript;

import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SqriptUtils {

    public static double getX(TypeArray array){
        if(array.getObject().size()>0)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(0))).getObject();
        else return 0;
    }

    public static double getY(TypeArray array){
        if(array.getObject().size()>1)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(1))).getObject();
        else return 0;
    }

    public static double getZ(TypeArray array){
        if(array.getObject().size()>2)
            return ((TypeNumber) (((ArrayList) (array.getObject())).get(2))).getObject();
        else return 0;
    }

    public static Vec3d arrayToLocation(ArrayList list){
        if(list.size()==0)
            return new Vec3d(0,0,0);
        if(list.size()==1)
            return new Vec3d(((TypeNumber)list.get(0)).getObject(),0,0);
        if(list.size()==2)
            return new Vec3d(((TypeNumber)list.get(0)).getObject(),((TypeNumber)list.get(1)).getObject(),0);
        else
            return new Vec3d(((TypeNumber)list.get(0)).getObject(),((TypeNumber)list.get(1)).getObject(),((TypeNumber)list.get(2)).getObject());
    }

    public static ArrayList locactionToArray(double x, double y, double z){
        return new ArrayList(Arrays.asList(new TypeNumber(x),new TypeNumber(y),new TypeNumber(z)));
    }

    public static ArrayList locactionToArray(BlockPos pos){
        return locactionToArray(pos.getX(),pos.getY(),pos.getZ());
    }

    public static ArrayList locactionToArray(EntityPlayer player){
        return locactionToArray(player.posX,player.posY,player.posZ);
    }

    public static void generateDoc() throws IOException {
        File doc = new File(ScriptManager.scriptDir,"doc.md");

        //Delete the content of the file
        new PrintWriter(doc).close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(doc, true));

        bw.write("**Events**\n");
        for(EventDefinition eventDefinition : ScriptManager.events){
            for (String pattern : eventDefinition.getPatterns()){
                bw.write("{"+eventDefinition.getName()+"} "+ Arrays.toString(eventDefinition.getAccessors()) +" " +eventDefinition.getSide()+" "+"("+Arrays.stream(eventDefinition.getEventClass().getAnnotations()).map(a->a.annotationType().getSimpleName()).collect(Collectors.joining(","))+") `"+pattern+"`\n");
            }
        }
        
        bw.write("\n");
        bw.write("**Actions**\n");
        for(ActionDefinition actionDefinition : ScriptManager.actions){
            for (String pattern : actionDefinition.getPatterns()){
                bw.write("{"+actionDefinition.getName()+"} " +actionDefinition.getSide()+" "+"("+Arrays.stream(actionDefinition.getActionClass().getAnnotations()).map(a->a.annotationType().getSimpleName()).collect(Collectors.joining(","))+") `"+pattern+"`\n");
            }
        }

        bw.write("\n");
        bw.write("**Expressions**\n");
        for(ExpressionDefinition expressionDefinition : ScriptManager.expressions){
            for (String pattern : expressionDefinition.getPatterns()){
                bw.write("{"+expressionDefinition.getName()+"} "+expressionDefinition.getSide()+" "+"("+Arrays.stream(expressionDefinition.getExpressionClass().getAnnotations()).map(a->a.annotationType().getSimpleName()).collect(Collectors.joining(","))+") `"+pattern+"`\n");
            }
        }

        bw.write("\n");
        bw.write("**Blocks**\n");
        for(BlockDefinition blockDefinition : ScriptManager.blocks){
            bw.write("{"+blockDefinition.getName()+"} " +blockDefinition.getSide()+" "+blockDefinition.isReloadable()+" "+"("+Arrays.stream(blockDefinition.getBlockClass().getAnnotations()).map(a->a.annotationType().getSimpleName()).collect(Collectors.joining(","))+")");
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    }


    public static <T> T rawInstantiation(Class<?> parent, Class<T> child) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor objDef = parent.getDeclaredConstructor();
        Constructor intConstr = rf.newConstructorForSerialization(child, objDef);
        return child.cast(intConstr.newInstance());
    }
}
