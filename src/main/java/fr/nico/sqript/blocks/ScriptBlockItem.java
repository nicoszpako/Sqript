package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.forge.common.item.*;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


@Block(
        feature = @Feature(name = "Item",
                description = "DEPRECATED : Use the register action instead. Define an item that will be added to the game, that you can give a certain behavior to.",
                examples = "item test_item:\n" +
                        "    name: My Test Item\n" +
                        "    texture: sample:yellow_diamond.png\n" +
                        "    creative tab: miscellaneous\n" +
                        "    max stack size: 8\n" +
                        "    type: item",
                regex = "^item .*",
                side = Side.BOTH),
        fields = {
                @Feature(name = "name"),
                @Feature(name = "texture"),
                @Feature(name = "max stack size"),
                @Feature(name = "creative tab"),
                @Feature(name = "item type"),
                @Feature(name = "material"),
                @Feature(name = "protection type"),
                @Feature(name = "armor texture"),
        },
        reloadable = false
)
public class ScriptBlockItem extends ScriptBlock {


    public ScriptBlockItem(ScriptToken head) throws ScriptException {
        super(head);
    }

    @Override
    protected void load() throws Exception {
        String registryName = getHead().getText().replaceFirst("item\\s+(.*)", "$1").replaceAll(":", "").replaceAll(" ", "_").trim();
        String texture = "";
        String model = "";
        String armorTexture;
        int maxStackSize = 1;
        CreativeTabs tab = CreativeTabs.MISC;
        String toolType = "item";
        String toolMaterial = "iron";
        String displayName;


        if (!fieldDefined("name"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(), "item", "name");
        if (!fieldDefined("texture") && !fieldDefined("model"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(), "item", "texture or model");

        displayName = getSubBlock("name").getRawContent();

        if (fieldDefined("texture"))
            texture = getSubBlock("texture").getRawContent().replaceAll("\\.png", "");
        if (fieldDefined("model"))
            model = getSubBlock("model").getRawContent();
        if (fieldDefined("max stack size"))
            maxStackSize = Integer.parseInt(getSubBlock("max stack size").getRawContent());
        if (fieldDefined("creative tab"))
            tab = loadTabFromName(getSubBlock("creative tab").getRawContent());
        if (fieldDefined("item type"))
            toolType = getSubBlock("item type").getRawContent();
        else
            //System.out.println("ITEM TYPE FIELD IS NOT DEFINED");
            if (fieldDefined("material"))
                toolMaterial = getSubBlock("material").getRawContent();
        //System.out.println("TOOLTYPE : "+toolType);
        Item item;
        switch (toolType.toLowerCase()) {
            case "axe":
                item = new ScriptItemAxe(displayName, toolMaterial);
                break;
            case "pickaxe":
                item = new ScriptItemPickaxe(displayName, toolMaterial);
                break;
            case "sword":
                item = new ScriptItemSword(displayName, toolMaterial);
                break;
            case "shovel":
                item = new ScriptItemShovel(displayName, toolMaterial);
                break;
            case "hoe":
                item = new ScriptItemHoe(displayName, toolMaterial);
                break;
            case "armor":
                if (fieldDefined("armor texture"))
                    armorTexture = getSubBlock("armor texture").getRawContent();
                else throw new ScriptException.ScriptMissingFieldException(getLine(), "item", "armor texture");
                //feet / legs / chest / head
                armorTexture = armorTexture.split(":")[0] + ":textures/" + armorTexture.split(":")[1];
                String protectionType = "chest";
                if (fieldDefined("protection type"))
                    protectionType = getSubBlock("protection type").getRawContent();
                else throw new ScriptException.ScriptMissingFieldException(getLine(), "item", "protection type");
                //System.out.println("ARMOR ITEM : "+protectionType+" "+ EntityEquipmentSlot.valueOf(protectionType.toUpperCase()));
                item = new ScriptItemArmor(displayName, toolMaterial, EntityEquipmentSlot.valueOf(protectionType.toUpperCase()), armorTexture);
                break;
            case "item":
            default:
                item = new ScriptItemBase(displayName);
        }
        item.setRegistryName(this.getHead().getScriptInstance().getName(), registryName);
        texture = this.getHead().getScriptInstance().getName() + ":" + texture;

        //System.out.println("Max stack size of item is : "+maxStackSize);
        item.setMaxStackSize(maxStackSize);
        item.setCreativeTab(tab);
        SqriptForge.scriptItems.add(new ScriptItem(this.getHead().getScriptInstance().getName(), model, item));

        createItem(registryName, texture, toolType);

    }

    private void createItem(String registryName, String texture, String toolType) throws Exception {
        //Creating the model/blockstate file if the model file was not specified
        //System.out.println("Creating model file for item : "+registryName);
        if (!fieldDefined("model")) {
            //File scriptFile
            File mainFolder = new File(ScriptManager.scriptDir, this.getHead().getScriptInstance().getName());
            if (!mainFolder.exists())
                mainFolder.mkdir();

            File itemModelsFolder = new File(mainFolder, "/models/item");
            if (!itemModelsFolder.exists())
                itemModelsFolder.mkdirs();
            //Il faut créer le dossier et l'enregistrer à la main
            String parent = toolType.equalsIgnoreCase("item") || toolType.equalsIgnoreCase("armor") ? "generated" : "handheld";
            File jsonFile = new File(itemModelsFolder, registryName + ".json");
            String content = ("{" + "\n"
                    + "  'parent': 'item/" + parent + "'," + "\n"
                    + "  'textures': {" + "\n"
                    + "      'layer0': '" + texture + "'" + "\n"
                    + "  }" + "\n"
                    + "}").replaceAll("'", "\"");
            createFile(jsonFile, content);
        }
    }

    private CreativeTabs loadTabFromName(String creative_tab) {
        System.out.println("Getting creative tab from : " + creative_tab);
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (tab.getTabLabel().equalsIgnoreCase(creative_tab) || creative_tab.startsWith(tab.getTabLabel()))
                return tab;
        }
        //System.out.println("Returning null for tab");
        return null;
    }


    private void createFile(File file, String content) throws Exception {
        if (file.exists()) {
            return;
        }
        //System.out.println("Creating file : "+file.getAbsolutePath());
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(content);
        out.close();
    }

    @Block(
            feature = @Feature(name = "Tool material",
                    description = "Define a tool material that you will be able to apply on your items in order to give them a durability and an efficiency.",
                    examples = "tool material ruby:",
                    regex = "^tool material .*",
                    side = Side.BOTH),
            fields = {
                    @Feature(name = "name"),
                    @Feature(name = "harvest level"),
                    @Feature(name = "durability"),
                    @Feature(name = "efficiency"),
                    @Feature(name = "damage"),
                    @Feature(name = "enchantability"),
            },
            reloadable = false)
    public static class ScriptBlockToolMaterial extends ScriptBlock {

        public ScriptBlockToolMaterial(ScriptToken head) throws ScriptException {
            super(head);
        }

        @Override
        protected void load() throws Exception {
            String name = getHead().getText().replaceAll("^tool material (.*):", "$1");

            int harvestLevel = Item.ToolMaterial.IRON.getHarvestLevel();
            int durability = Item.ToolMaterial.IRON.getMaxUses();
            float efficiency = Item.ToolMaterial.IRON.getEfficiency();
            float damage = Item.ToolMaterial.IRON.getAttackDamage();
            int enchantability = Item.ToolMaterial.IRON.getEnchantability();

            if (fieldDefined("harvest level"))
                harvestLevel = Integer.parseInt(getSubBlock("harvest level").getRawContent());
            if (fieldDefined("durability"))
                durability = Integer.parseInt(getSubBlock("durability").getRawContent());
            if (fieldDefined("efficiency"))
                efficiency = Float.parseFloat(getSubBlock("efficiency").getRawContent());
            if (fieldDefined("damage"))
                damage = Float.parseFloat(getSubBlock("damage").getRawContent());
            if (fieldDefined("enchantability"))
                enchantability = Integer.parseInt(getSubBlock("enchantability").getRawContent());

            ScriptManager.log.info("Registering tool material : " + name.toUpperCase());
            EnumHelper.addToolMaterial(name, harvestLevel, durability, efficiency, damage, enchantability);
        }

    }

    @Block(
            feature = @Feature(name = "Armor material",
                    description = "Define an armor material that you will be able to apply on your armor pieces in order to give them a durability and a protection coefficient.",
                    examples = "armor material ruby:",
                    regex = "^armor material .*",
                    side = Side.BOTH),
            fields = {
                    @Feature(name = "name"),
                    @Feature(name = "durability"),
                    @Feature(name = "protection array"),
                    @Feature(name = "enchantability"),
            },
            reloadable = false)
    public static class ScriptBlockArmorMaterial extends ScriptBlock {


        public ScriptBlockArmorMaterial(ScriptToken head) throws ScriptException {
            super(head);
        }

        @Override
        protected void load() throws Exception {
            String name = getHead().getText().replaceAll("^armor material (.*):", "$1");

            int durabilityfactor = 15;
            int[] protection = new int[]{
                    ItemArmor.ArmorMaterial.IRON.getDamageReductionAmount(EntityEquipmentSlot.FEET),
                    ItemArmor.ArmorMaterial.IRON.getDamageReductionAmount(EntityEquipmentSlot.LEGS),
                    ItemArmor.ArmorMaterial.IRON.getDamageReductionAmount(EntityEquipmentSlot.CHEST),
                    ItemArmor.ArmorMaterial.IRON.getDamageReductionAmount(EntityEquipmentSlot.HEAD),
            };
            int enchantability = Item.ToolMaterial.IRON.getEnchantability();


            if (fieldDefined("durability"))
                durabilityfactor = Integer.parseInt(getSubBlock("durability").getRawContent());
            if (fieldDefined("protection array"))
                protection = (((TypeArray) (getSubBlock("protection array").evaluate())).getObject()).stream().mapToInt(a -> (Integer) a.getObject()).toArray();
            if (fieldDefined("enchantability"))
                enchantability = Integer.parseInt(getSubBlock("enchantability").getRawContent());

            ScriptManager.log.info("Registering armor material : " + name.toUpperCase());
            EnumHelper.addArmorMaterial(name.toUpperCase(), "", durabilityfactor, protection, enchantability, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
        }

    }

}
