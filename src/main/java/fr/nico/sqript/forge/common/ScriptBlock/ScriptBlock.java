package fr.nico.sqript.forge.common.ScriptBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class ScriptBlock extends Block{

    ResourceLocation drop;
    int harvestLevel;

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public ScriptBlock(Material materialIn, ResourceLocation drop, int harvestLevel) {
        super(materialIn);
        this.drop = drop;
        this.harvestLevel = harvestLevel;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if(drop == null)
            return ForgeRegistries.ITEMS.getValue(getRegistryName());
        return ForgeRegistries.ITEMS.getValue(drop);
    }
}
