package fr.nico.sqript.forge.common.item;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ScriptItemAxe extends ItemAxe {

    public final String displayName;

    public ScriptItemAxe(String displayName, String toolMaterial){
        super(ToolMaterial.valueOf(toolMaterial.toUpperCase()));
        this.displayName = displayName;

    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}