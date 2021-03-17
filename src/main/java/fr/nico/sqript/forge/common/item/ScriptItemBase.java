package fr.nico.sqript.forge.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ScriptItemBase extends Item {

    public final String registryName, displayName, modid, customModel;

    public ScriptItemBase(String registryName, String modid,String displayName){
        super();
        this.registryName = registryName;
        this.displayName = displayName;
        this.modid = modid;
        this.customModel = "";
    }

    public ScriptItemBase(String registryName, String modid,String displayName, String customModel){
        super();
        this.registryName = registryName;
        this.displayName = displayName;
        this.modid = modid;
        this.customModel = customModel;
    }


    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }
}
