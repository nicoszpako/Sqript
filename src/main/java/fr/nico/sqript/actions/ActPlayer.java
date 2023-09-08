package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.TypeItemStack;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

@Action(name = "Player Actions",
        features = {
            @Feature(name = "Teleport player", description = "Teleports a player to a given location.", examples = "teleport player at [10,25,20]", pattern = "teleport {player} (to|at) {array}"),
            @Feature(name = "Give item to player", description = "Gives an item to a player.", examples = "give a[n] minecraft:diamond_sword to player\n", pattern = "give {item} to {player}"),
            @Feature(name = "Kick player", description = "Kicks a player from the server.", examples = "kick player with message \"You've been kicked for cheating\"", pattern = "kick {player} [with message {string}]"),
            @Feature(name = "Remove item from player's inventory", description = "Attempts to remove the given item from player's inventory.", examples = "remove 1 of diamond from player's inventory", pattern = "remove [{number} of] {itemtype} from {player}['s] inventory")
        }
)
public class ActPlayer extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                EntityPlayer player = (EntityPlayer) getParameters().get(0).get(context).getObject();
                ArrayList pos = (ArrayList) getParameters().get(1).get(context).getObject();
                player.setPositionAndUpdate(((TypeNumber)pos.get(0)).getObject(), ((TypeNumber)pos.get(1)).getObject(),((TypeNumber)pos.get(2)).getObject());
                return;
            case 1:
                ItemStack item = getParameterOrDefault(1, ItemStack.EMPTY, context, TypeItemStack.class);
                player = (EntityPlayer) getParameter(2).get(context).getObject();
                player.addItemStackToInventory(item);
                break;
            case 2:
                EntityPlayerMP playermp = (EntityPlayerMP) getParameter(0,context);
                playermp.connection.disconnect(new TextComponentString(getParametersSize()==2?((String) getParameter(1,context)).replaceAll("&","\247"):""));
                break;
            case 3:
                int amount = getParameterOrDefault(getParameter(1),1,context);
                Item itemtype = ScriptManager.parse(getParameter(2).get(context), TypeItem.class).getObject();
                playermp = (EntityPlayerMP) getParameter(3,context);
                playermp.inventory.clearMatchingItems(itemtype,-1,amount,null);

        }
    }
}