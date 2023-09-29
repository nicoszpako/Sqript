package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

@Expression(name = "Items Expressions",
        features = {
            @Feature(name = "Item stack", description = "Returns a stack of the given amount of the given item.", examples = "5 of minecraft:stick", pattern = "(a[n]|{+number} of) {resource} [with nbt {string|nbttagcompound}] [[and] with metadata {number}]", type = "item", settable = false),
            @Feature(name = "Player has item", description = "Returns whether the player has the given item in his inventory.", examples = "5 of minecraft:stick is in player's inventory", pattern = "({item} is in {player}['s] inventory|{player} has {item})", type = "boolean", settable = false)
        }
)
public class ExprItems extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        //System.out.println(Arrays.toString(parameters));
        switch(getMatchedName()){
            case "Item stack":
                //System.out.println(Arrays.toString(parameters));
                int amount = getParameterOrDefault(parameters[0], 1d).intValue();
                Item item = ScriptManager.parse(parameters[1],TypeItem.class).getObject();
                int metadata = getParameterOrDefault(parameters[3], 0d).intValue();
                ItemStack itemStack = new ItemStack(item, amount, metadata);
                if(parameters[2] != null) {
                    if(parameters[2] instanceof TypeString) {
                        String data = (String) parameters[2].getObject();
                        try {
                            itemStack.setTagCompound(JsonToNBT.getTagFromJson(data));
                        } catch (NBTException e) {
                            e.printStackTrace();
                        }
                    } else if(parameters[2] instanceof TypeNBTTagCompound) {
                        itemStack.setTagCompound((NBTTagCompound) parameters[2].getObject());
                    }
                }
                return new TypeItemStack(itemStack);
            case "Player has item":
                EntityPlayerMP playerMP = null;
                itemStack = null;
                if(parameters[0] != null){
                    itemStack = ScriptManager.parse(parameters[0],TypeItemStack.class).getObject();
                    playerMP = (EntityPlayerMP) parameters[1].getObject();
                }
                if (parameters[3] != null){
                    itemStack = ScriptManager.parse(parameters[3],TypeItemStack.class).getObject();
                    playerMP = (EntityPlayerMP) parameters[2].getObject();
                }
                return new TypeBoolean(playerMP.inventory.hasItemStack(itemStack));
        }
        return null;
    }

    @Override
    public boolean validate(String[] parameters, ScriptToken line) {
        return true;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
        }
        return false;
    }
}
