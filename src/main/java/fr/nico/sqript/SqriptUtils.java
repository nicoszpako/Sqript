package fr.nico.sqript;

import fr.nico.sqript.meta.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        bw.write("**Events**\n\n");
        for(EventDefinition eventDefinition : ScriptManager.events){
            for (String pattern : eventDefinition.getPatterns()){
                bw.write("`"+pattern+"`"+" *ex: "+ Arrays.toString(eventDefinition.getExample()) +"* \n\n");
            }
        }
        
        bw.write("\n\n");
        bw.write("**Actions**\n\n");
        for(ActionDefinition actionDefinition : ScriptManager.actions){
            for (String pattern : actionDefinition.getPatterns()){
                bw.write("`"+pattern+"`"+" *ex: "+ Arrays.toString(actionDefinition.getExample()) +"* \n\n");
            }
        }

        bw.write("\n\n");
        bw.write("**Expressions**\n\n");
        for(ExpressionDefinition expressionDefinition : ScriptManager.expressions){
            for (String pattern : expressionDefinition.getPatterns()){
                bw.write("`"+pattern+"` *ex: "+ Arrays.toString(expressionDefinition.getExample()) +"* \n\n");
            }
        }

        bw.write("\n\n");
        bw.write("**Blocks**\n\n");
        for(BlockDefinition blockDefinition : ScriptManager.blocks){
            bw.write(blockDefinition.getName()+" *ex: "+blockDefinition.getDescription()+"*");
            for (String pattern : blockDefinition.getExample()){
                bw.write("`"+pattern+"` ");
            }
            bw.write("\n\n");
        }
        bw.flush();
        bw.close();
    }
}
