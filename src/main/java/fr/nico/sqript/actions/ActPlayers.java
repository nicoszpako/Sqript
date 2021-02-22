package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeBlockPos;
import fr.nico.sqript.types.TypeItemStack;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Action(name = "Network Actions",
        description ="Network related actions",
        examples = {"teleport player at location at 5 8 9"
        },
        patterns = {
                "teleport {player} to {blockpos}",
                "give {resource} to {player}",
        }
)
public class ActPlayers extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                EntityPlayer player = (EntityPlayer) getParameters().get(0).get(context).getObject();
                TypeBlockPos pos = (TypeBlockPos) getParameters().get(1).get(context);
                player.setPositionAndUpdate(pos.getObject().getX(), pos.getObject().getY(), pos.getObject().getZ());
                return;
            case 1:
                ScriptType param = getParameter(1).get(context);
                ItemStack item = null;
                if (param instanceof TypeResource) {
                    Item i = ForgeRegistries.ITEMS.getValue(((TypeResource) (param)).getObject());
                    if (i == null) {
                        ScriptManager.log.error("No item found for identifier : " + param.getObject().toString());
                        return;
                    }
                    item = new ItemStack(i);
                }
                if (param instanceof TypeItemStack) {
                    item = ((TypeItemStack) (param)).getObject();
                }
                if(item==null)
                    return;
                player = (EntityPlayer) getParameter(2).get(context).getObject();
                player.addItemStackToInventory(item);
        }
    }
}