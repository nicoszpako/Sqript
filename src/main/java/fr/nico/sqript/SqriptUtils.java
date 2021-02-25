package fr.nico.sqript;

import fr.nico.sqript.meta.ActionDefinition;
import fr.nico.sqript.meta.BlockDefinition;
import fr.nico.sqript.meta.ExpressionDefinition;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SqriptUtils {

    public static ArrayList locactionToArray(double x, double y, double z){
        return (ArrayList) Arrays.asList(new TypeNumber(x),new TypeNumber(y),new TypeNumber(z));
    }

    public static ArrayList locactionToArray(EntityPlayer player){
        return locactionToArray(player.posX,player.posY,player.posZ);
    }

    public static void generateDoc() throws IOException {
        File doc = new File(ScriptManager.scriptDir,"doc.md");

        //Delete the content of the file
        new PrintWriter(doc).close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(doc, true));
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
