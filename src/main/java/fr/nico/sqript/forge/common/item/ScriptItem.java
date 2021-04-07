package fr.nico.sqript.forge.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ScriptItem {

    //Wrapper for item classes

    public final String modid, customModel;
    public final Item item;

    public ScriptItem(String modid, String customModel, Item item){
        this.modid = modid;
        this.customModel = customModel;
        this.item = item;
    }


    public String getModid() {
        return modid;
    }

    public String getCustomModel() {
        return customModel;
    }

    public Item getItem() {
        return item;
    }
}
