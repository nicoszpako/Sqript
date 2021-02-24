package fr.nico.sqript;

import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Arrays;

public class SqriptUtils {
    public static ArrayList locactionToArray(double x, double y, double z){
        return (ArrayList) Arrays.asList(new TypeNumber(x),new TypeNumber(y),new TypeNumber(z));
    }
    public static ArrayList locactionToArray(EntityPlayer player){
        return locactionToArray(player.posX,player.posY,player.posZ);
    }
}
