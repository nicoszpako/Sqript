package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeItemStack;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;

@Action(name = "Drop",
        features = {
            @Feature(name = "Drop an item",description = "Drops an item somewhere.",examples = "drop 1 minecraft:diamond_sword at player's location", pattern = "drop [{+number}] {item} at {array}")
        }
)
public class ActDrop extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                int amount = getParameterOrDefault(getParameter(1),1d, context).intValue();
                ScriptType param = getParameter(2).get(context);
                ItemStack item = null;
                if (param instanceof TypeResource) {
                    Item i = ForgeRegistries.ITEMS.getValue(((TypeResource) (param)).getObject());
                    if (i == null) {
                        i = Item.getItemFromBlock(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(((TypeResource) (param)).getObject())));
                        if (i == null){
                            ScriptManager.log.error("No item found for identifier : " + param.getObject().toString());
                            return;
                        }
                    }
                    item = new ItemStack(i,amount);
                }
                if (param instanceof TypeItemStack) {
                    item = ((TypeItemStack) (param)).getObject();
                }
                if(item==null)
                    return;
                ILocatable locatable = (ILocatable) getParameter(3).get(context);
                World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
                EntityItem entityItem = new EntityItem(world,locatable.getVector().x,locatable.getVector().y,locatable.getVector().z,item);
                world.spawnEntity(entityItem);
        }

    }
}
