package fr.nico.sqript.actions;


import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.forge.common.item.ScriptItem;
import fr.nico.sqript.forge.common.item.ScriptItemBase;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Action(name = "Registery Actions",
        features = {
                @Feature(name = "Register a new item", description = "Creates and registers a new item in the game.", examples = "register a new item named \"Copper\" with texture \"copper_item\"", pattern = "register [a] [new] item named {string} [with (texture {resource}|model {resource})] [with [max] stack size {number}] [in tab {string}]", side = Side.CLIENT),
                @Feature(name = "Register a new block", description = "Creates and registers a new block in the game.", examples = "register a new block named \"Copper Ore\" with texture \"copper_ore\"", pattern = "register [a] [new] block named {string} [with (texture {resource}|model {resource})] [with [max] stack size {number}] [with hardness {number}] [with harvest level {number}] [with material {string}] [dropping {resource}] [in tab {string}]", side = Side.CLIENT),
        }
)
public class ActRegistery extends ScriptAction {

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0: //Item
                registerNewItem(context);
                break;
            case 1: //Block
                registerNewBlock(context);
                break;
        }
    }

    private void registerNewBlock(ScriptContext context) throws ScriptException {
        String name = (String) getParameter(1).get(context).getObject();
        String registryName = SqriptUtils.getIdentifier(name);
        ResourceLocation blockTexture = getParameterOrDefault(getParameter(2), null, context);
        ResourceLocation blockName = getParameterOrDefault(getParameter(3), null, context);
        int maxStackSize = getParameterOrDefault(getParameter(4), 64, context);
        float hardness = getParameterOrDefault(getParameter(5), 0, context);
        int harvestLevel = getParameterOrDefault(getParameter(6), 0, context);
        String materialName = getParameterOrDefault(getParameter(7), "iron", context);
        ResourceLocation drop =  getParameterOrDefault(getParameter(8), null, context);
        CreativeTabs creativeTab = loadTabFromName(getParameterOrDefault(getParameter(9), "misc", context));

        Material material = Material.ROCK;
        fr.nico.sqript.forge.common.ScriptBlock.ScriptBlock block = new fr.nico.sqript.forge.common.ScriptBlock.ScriptBlock(material,drop,harvestLevel);
        block.setRegistryName(getLine().getScriptInstance().getName(),registryName);
        block.setUnlocalizedName(registryName);
        block.setHardness(hardness);
        block.setCreativeTab(creativeTab);

        //System.out.println("Max stack size of item is : "+maxStackSize);
        SqriptForge.blocks.add(block);

        String itemTextureString = blockTexture == null ? "" : blockTexture.toString().replaceAll("\\.png","");

        try {
            if(blockTexture!=null)
                createBlockModel(registryName, itemTextureString);
            createBlockState(registryName,itemTextureString);
            createItemBlockModel(registryName,itemTextureString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Item itemBlock = new ItemBlock(block){
            @Override
            public String getItemStackDisplayName(ItemStack stack) {
                return name;
            }
        }.setUnlocalizedName(block.getUnlocalizedName()).setRegistryName(block.getRegistryName());
        SqriptForge.items.add(itemBlock);
    }

    private void registerNewItem(ScriptContext context) throws ScriptException {
        String name = (String) getParameter(1).get(context).getObject();
        String registryName = SqriptUtils.getIdentifier(name);
        ResourceLocation itemTexture = getParameterOrDefault(getParameter(2), null, context);
        ResourceLocation itemModel = getParameterOrDefault(getParameter(3), null, context);
        int itemStackSize = getParameterOrDefault(getParameter(4), 64, context);
        CreativeTabs creativeTab = loadTabFromName(getParameterOrDefault(getParameter(5), "misc", context));

        Item item = new ScriptItemBase(name);
        item.setRegistryName(getLine().getScriptInstance().getName(), registryName);
        item.setUnlocalizedName(registryName);
        //System.out.println("Max stack size of item is : "+maxStackSize);
        item.setMaxStackSize(itemStackSize);
        item.setCreativeTab(creativeTab);
        String toolType = "item";
        String itemTextureString =  itemTexture.toString().replaceAll("\\.png","");
        if (itemTexture != null)
            try {
                createItemModel(registryName, itemTextureString, toolType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        SqriptForge.scriptItems.add(new ScriptItem(getLine().getScriptInstance().getName(), itemModel == null ? "" : itemModel.toString(), item));
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

    private void createItemModel(String registryName, String texture, String toolType) throws Exception {
        //Creating the model/blockstate file if the model file was not specified
        //System.out.println("Creating model file for item : "+registryName);
        //File scriptFile
        File mainFolder = new File(ScriptManager.scriptDir,getLine().getScriptInstance().getName());
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
        createFileIfNotExist(jsonFile, content);
    }

    private void createBlockModel(String registryName, String texture) throws Exception {
        //Creating the model/blockstate file if the model file was not specified
        //System.out.println("Creating model file for item : "+registryName);
            //File scriptFile
            File mainFolder = new File(ScriptManager.scriptDir,getLine().getScriptInstance().getName());
            if(!mainFolder.exists())
                mainFolder.mkdir();

            File itemModelsFolder = new File(mainFolder,"/models/block");
            if(!itemModelsFolder.exists())
                itemModelsFolder.mkdirs();
            //Il faut créer le dossier et l'enregistrer à la main
            String parent = "minecraft:block/cube_all";
            File jsonFile = new File(itemModelsFolder,registryName+".json");
            String content = ("{"+"\n"
                    +"  'parent': '"+parent+"',"+"\n"
                    +"  'textures': {"+"\n"
                    +"      'all': '"+texture+"'"+"\n"
                    +"  }"+"\n"
                    +"}").replaceAll("'","\"");
            createFileIfNotExist(jsonFile,content);
    }

    private void createBlockState(String registryName, String texture) throws Exception {
        //Creating the model/blockstate file if the model file was not specified
        //System.out.println("Creating model file for item : "+registryName);

            //File scriptFile
            File mainFolder = new File(ScriptManager.scriptDir,getLine().getScriptInstance().getName());
            if(!mainFolder.exists())
                mainFolder.mkdir();

            File itemModelsFolder = new File(mainFolder,"/blockstates/");
            if(!itemModelsFolder.exists())
                itemModelsFolder.mkdirs();
            //Il faut créer le dossier et l'enregistrer à la main
            File jsonFile = new File(itemModelsFolder,registryName+".json");
            String content = ("{"+"\n"
                    +"    \"variants\": {\n"
                    +"        \"normal\": [\n"
                    +"            { \"model\": \""+getLine().getScriptInstance().getName()+":"+registryName+"\" }\n"
                    +"        ]\n"
                    +"    }\n"
                    +"}"
            ).replaceAll("'","\"");
            createFileIfNotExist(jsonFile,content);
    }


    private void createItemBlockModel(String registryName, String texture) throws Exception {
        //Creating the model/blockstate file if the model file was not specified
        //System.out.println("Creating model file for item : "+registryName);
            //File scriptFile
            File mainFolder = new File(ScriptManager.scriptDir, getLine().getScriptInstance().getName());
            if(!mainFolder.exists())
                mainFolder.mkdir();

            File itemModelsFolder = new File(mainFolder,"/models/item");
            if(!itemModelsFolder.exists())
                itemModelsFolder.mkdirs();
            //Il faut créer le dossier et l'enregistrer à la main
            File jsonFile = new File(itemModelsFolder,registryName+".json");
            String content = ("{"+"\n"
                    +"  'parent': '"+getLine().getScriptInstance().getName()+":block/"+registryName+"'\n"
                    +"}").replaceAll("'","\"");
            createFileIfNotExist(jsonFile,content);
    }



    private void createFileIfNotExist(File file, String content) throws Exception {
        if (file.exists()) {
            return;
        }
        //System.out.println("Creating file : "+file.getAbsolutePath());
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(content);
        out.close();
    }



}