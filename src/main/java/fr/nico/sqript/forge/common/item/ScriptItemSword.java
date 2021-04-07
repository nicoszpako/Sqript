package fr.nico.sqript.forge.common.item;

import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ScriptItemSword extends ItemSword {

    public final String displayName;

    public ScriptItemSword(String displayName, String toolMaterial){
        super(ToolMaterial.valueOf(toolMaterial.toUpperCase()));
        this.displayName = displayName;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}