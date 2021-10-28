package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.annotation.meta.param;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

@Expression(name = "Items Expressions",
        features = {
            @Feature(name = "Item NBT tag", description = "Returns the NBT tag of the given item.", examples = "item's nbt", pattern = "{item}'s nbt [tag]", type = "nbttagcompound"),
            @Feature(name = "Item", description = "Returns the item associated to the given resource.", examples = "minecraft:stick", pattern = "{resource} [with data {string}]", type = "itemdata", settable = false),
            @Feature(name = "Item stack", description = "Returns a stack of the given amount of the given item.", examples = "5 of minecraft:stick", pattern = "[(a|{+number})] [of] {itemdata} [with nbt {string|nbttagcompound}] [with metadata {number}]", type = "item", settable = false)
        }, priority = -2
)
public class ExprItems extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        //System.out.println(Arrays.toString(parameters));
        switch(getMatchedName()){
            case "Item NBT tag":
                ItemStack stack = (ItemStack) parameters[0].getObject();
                NBTTagCompound tagCompound = new NBTTagCompound();
                if(stack.hasTagCompound())
                    tagCompound = stack.getTagCompound();
                return new TypeNBTTagCompound(tagCompound);
            case "Item":
                ResourceLocation resource = (ResourceLocation) parameters[0].getObject();
                Item i = ForgeRegistries.ITEMS.getValue(resource);
                return new TypeItemData(i);
            case "Item stack":
                int amount = getParameterOrDefault(parameters[0], 1d).intValue();
                Item item = (Item) parameters[1].getObject();
                ItemStack itemStack = new ItemStack(item, amount);
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
                return new TypeItem(itemStack);

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
            case 0:
                if(to instanceof TypeNBTTagCompound){
                    ItemStack stack = (ItemStack) parameters[0].getObject();
                    stack.setTagCompound((NBTTagCompound) to.getObject());
                } else {
                    ScriptManager.log.error("You can only set a NBTTagCompound in parameter");
                }
                return true;
        }
        return false;
    }
}
