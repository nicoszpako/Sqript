package fr.nico.sqript.forge.common.item;

import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ScriptItemHoe extends ItemHoe {

    public final String displayName;

    public ScriptItemHoe(String displayName, String toolMaterial){
        super(ToolMaterial.valueOf(toolMaterial.toUpperCase()));
        this.displayName = displayName;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}