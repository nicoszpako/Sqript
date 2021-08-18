package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Objects;

@Action(name = "Player Actions",
        features = {
            @Feature(name = "Teleport player", description = "Teleports a player to a given location.", examples = "teleport player at [10,25,20]", pattern = "teleport {player} to {array}"),
            @Feature(name = "Give item to player", description = "Gives an item to a player.", examples = "give 1 minecraft:diamond_sword to player\n", pattern = "give [{+number}] {item} to {player}"),
            @Feature(name = "Kick player", description = "Kicks a player from the server.", examples = "kick player with message \"You've been kicked for cheating\"", pattern = "kick {player} [with message {string}]")
        }
)
public class ActPlayer extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                EntityPlayer player = (EntityPlayer) getParameters().get(0).get(context).getObject();
                ArrayList pos = (ArrayList) getParameters().get(1).get(context).getObject();
                player.setPositionAndUpdate((double)pos.get(0), (double)pos.get(1),(double) pos.get(2));
                return;
            case 1:
                int amount = getParameterOrDefault(getParameter(1),1, context);
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
                if (param instanceof TypeItem) {
                    item = ((TypeItem) (param)).getObject();
                }
                if(item==null)
                    return;
                player = (EntityPlayer) getParameter(2).get(context).getObject();
                player.addItemStackToInventory(item);
                break;
            case 2:
                EntityPlayerMP playermp = (EntityPlayerMP) getParameter(0,context);
                playermp.connection.disconnect(new TextComponentString(getParametersSize()==2?((String) getParameter(1,context)).replaceAll("&","\247"):""));
                break;
        }
    }
}