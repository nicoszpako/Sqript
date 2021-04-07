package fr.nico.sqript.forge.common.item;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ScriptItemPickaxe extends ItemPickaxe {

    public final String displayName;

    public ScriptItemPickaxe(String displayName, String toolMaterial){
        super(ToolMaterial.valueOf(toolMaterial.toUpperCase()));
        this.displayName = displayName;

    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}