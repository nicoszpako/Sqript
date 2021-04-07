package fr.nico.sqript.forge.common.item;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ScriptItemShovel extends ItemSpade {

    public final String displayName;

    public ScriptItemShovel(String displayName, String toolMaterial){
        super(ToolMaterial.valueOf(toolMaterial.toUpperCase()));
        this.displayName = displayName;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}