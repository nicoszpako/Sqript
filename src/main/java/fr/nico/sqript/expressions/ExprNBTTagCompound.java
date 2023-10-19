package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashSet;
import java.util.Set;

@Expression(name = "NBTTagCompound",
        features = {
                @Feature(name = "NBTTagCompound", description = "Returns a new NBTTagCompound.", examples = "set {my_nbtcompound} to a new nbt compound", pattern = "[a] [new] nbt compound", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound from a json string", description = "Returns a new NBTTagCompound based on elements of a json string.", examples = "set {my_nbtcompound} to a new nbt compound with json \"{\"name\":\"test\"}\"", pattern = "[a] [new] nbt [tag] [compound] (with|from|of) json [string] {string}", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound from a dictionary", description = "Returns a new NBTTagCompound based on elements of a dictionary.", examples = "set {my_nbtcompound} to nbt from {dict}", pattern = "[a] [new] nbt [tag] [compound] from [dictionary] {dictionary}", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound value", description = "Returns value of key NBTTagCompound. A type can be specified, valid specific types are int, byte, float, short, long.", examples = "tag {string} of {nbt} as \"byte\"", pattern = "tag {string} of {nbttagcompound} [as {string}]", type="element"),
                @Feature(name = "NBTTagCompound copy", description = "Returns a copy of a NBTTagCompound.", examples = "a copy of {nbt}", pattern = "[a] copy [of] {nbttagcompound}", type="nbttagcompound"),
                @Feature(name = "Item or block nbt tag", description = "Returns a copy of the NBTTagCompound of a block or an item.", examples = "block at [4,54,-52] nbt tag", pattern = "{item|block}['s] nbt [tag] [in world {number}]", type="nbttagcompound"),
                @Feature(name = "NBTTagCompound keys", description = "Returns a list containing all the given NBTTagCompound keys.", examples = "block at [4,54,-52] nbt tag keys", pattern = "{nbttagcompound}['s] keys", type="array"),
                @Feature(name = "NBTTagCompound to a dictionary", description = "Returns a dictionary containing all the given NBTTagCompound keys and NBT values.", examples = "block at [4,54,-52] nbt tag dictionary", pattern = "{nbttagcompound}['s] dictionary", type="dictionary"),
        }
)
public class ExprNBTTagCompound extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                return new TypeNBTTagCompound(new NBTTagCompound());
            case 1:
                TypeString s = (TypeString) parameters[0];
                try {
                    return new TypeNBTTagCompound(JsonToNBT.getTagFromJson(s.getObject()));
                } catch (NBTException e) {
                    e.printStackTrace();
                    return new TypeNull();
                }
            case 2:
                TypeDictionary dictionary = (TypeDictionary) parameters[0];
                return new TypeNBTTagCompound(dictionary.write(new NBTTagCompound()));
            case 3:
                TypeNBTTagCompound tagCompound = (TypeNBTTagCompound) parameters[1];
                if(tagCompound == null)
                    return new TypeNull();
                return SqriptUtils.getTagFromTypeNBTTagCompound(tagCompound.getObject(), ((TypeString) parameters[0]).getObject());
            case 4:
                tagCompound = (TypeNBTTagCompound) parameters[0];
                return new TypeNBTTagCompound(tagCompound.getObject().copy());
            case 5:
                if (parameters[0] instanceof TypeItemStack){
                    ItemStack stack = (ItemStack) parameters[0].getObject();
                    if(stack.hasTagCompound())
                        return new TypeNBTTagCompound(stack.getTagCompound());
                }else if(parameters[0] instanceof TypeBlock){
                    int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                    TypeBlock block = (TypeBlock)parameters[0];
                    World world = SqriptUtils.getWorld(worldId);

                    TileEntity tileEntity = world.getTileEntity(block.getPos());
                    if (tileEntity == null)
                        return new TypeNull();
                    else
                        return new TypeNBTTagCompound(tileEntity.serializeNBT().copy());
                }
            case 6:
                tagCompound = (TypeNBTTagCompound) parameters[0];
                TypeArray typeArray = new TypeArray();
                for (String key : tagCompound.getObject().getKeySet()) {
                    typeArray.add(new TypeString(key));
                }
                return typeArray;
            case 7:
                tagCompound = (TypeNBTTagCompound) parameters[0];
                TypeDictionary typeDictionary = new TypeDictionary();
                for (String key : tagCompound.getObject().getKeySet()) {
                    typeDictionary.getObject().put(new TypeString(key),SqriptUtils.getTagFromTypeNBTTagCompound(tagCompound.getObject(),key));
                }
                return typeDictionary;
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptNullReferenceException {
        switch (getMatchedIndex()) {
            case 3:
                String key = (String) parameters[0].getObject();
                if(parameters[1] == null)
                    throw new ScriptException.ScriptNullReferenceException(line);
                NBTTagCompound nbtTagCompound = (NBTTagCompound) parameters[1].getObject();

                String type = "";
                if (parameters[2] != null)
                    type = (String) parameters[2].getObject();
                SqriptUtils.setTag(nbtTagCompound, key, type, to);
                return true;
            case 5:
                if (parameters[0] instanceof TypeBlock){
                    int worldId = parameters[1] == null ? 0 : (int) parameters[1].getObject();
                    TypeBlock block = ScriptManager.parse(parameters[0],TypeBlock.class);
                    World world = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[worldId];
                    TileEntity tileEntity = world.getTileEntity(block.getPos());
                    NBTTagCompound toNbt = ((TypeNBTTagCompound)(to)).getObject();
                    if(tileEntity != null){
                        tileEntity.readFromNBT(toNbt);
                        /*
                        NBTTagCompound nbt = tileEntity.getTileData();
                        Set<String> keySet = new HashSet<>(nbt.getKeySet());
                        keySet.forEach(k->nbt.removeTag());
                        System.out.println("Start adding");
                        toNbt.getKeySet().forEach(k->nbt.setTag(k,toNbt.getTag(k)));
                        tileEntity.markDirty();
                        */
                    }
                    return true;
                }else if (parameters[0] instanceof TypeItemStack){
                    ItemStack stack = (ItemStack) parameters[0].getObject();
                    stack.setTagCompound((NBTTagCompound) to.getObject());
                    return true;
                }
        }
        return false;
    }
}
