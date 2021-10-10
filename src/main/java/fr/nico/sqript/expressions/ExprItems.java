package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
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
            @Feature(name = "Item NBT tag", description = "Returns the NBT tag of the given item.", examples = "item's nbt", pattern = "{item}'s nbt [tag]", type = "dictionary"),
            @Feature(name = "Item", description = "Returns the item associated to the given resource.", examples = "minecraft:stick", pattern = "{resource} [with data {string}]", type = "itemdata", settable = false),
            @Feature(name = "Item stack", description = "Returns a stack of the given amount of the given item.", examples = "5 of minecraft:stick", pattern = "[(a|{+number})] [of] {itemdata} [with nbt {string}] [with metadata {number}]", type = "item", settable = false)
        }, priority = -2
)
public class ExprItems extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        //System.out.println(Arrays.toString(parameters));
        switch(getMatchedName()){
            case "Item NBT tag":
                ItemStack stack = (ItemStack) parameters[0].getObject();
                TypeDictionary dictionary;
                NBTTagCompound tagCompound = new NBTTagCompound();
                if(stack.hasTagCompound())
                    tagCompound = stack.getTagCompound();
                dictionary = SqriptUtils.NBTToDictionary(tagCompound);
                return dictionary;
            case "Item":
                ResourceLocation resource = (ResourceLocation) parameters[0].getObject();
                Item i = ForgeRegistries.ITEMS.getValue(resource);
                return new TypeItemData(i);
            case "Item stack":
                int amount = getParameterOrDefault(parameters[0], 1d).intValue();
                Item item = (Item) parameters[1].getObject();
                String data = getParameterOrDefault(parameters[2],"");
                NBTTagCompound tag = new NBTTagCompound();
                if(!data.isEmpty()){
                    InputStream targetStream = new ByteArrayInputStream(data.getBytes());
                    try {
                        tag = CompressedStreamTools.read(new DataInputStream(targetStream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ItemStack itemStack = new ItemStack(item,amount);
                itemStack.setTagCompound(tag);
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
        return false;
    }
}
