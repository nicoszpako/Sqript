package fr.nico.sqript.structures.prototypes;

import fr.nico.sqript.types.TypeItem;
import net.minecraft.creativetab.CreativeTabs;

public class ProtoypeBlock {

    String registryName = "";
    String displayName = "";
    String texture = "";
    CreativeTabs creativeTab = CreativeTabs.MISC;
    int harvestLevel = 0;
    int hardness = 0;
    TypeItem drop;

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public CreativeTabs getCreativeTab() {
        return creativeTab;
    }

    public void setCreativeTab(CreativeTabs creativeTab) {
        this.creativeTab = creativeTab;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public void setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
    }

    public int getHardness() {
        return hardness;
    }

    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    public TypeItem getDrop() {
        return drop;
    }

    public void setDrop(TypeItem drop) {
        this.drop = drop;
    }
}
