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
import fr.nico.sqript.types.TypeImage;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.primitive.TypeResource;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

@Action(name = "Registery Actions",
        features = {
                @Feature(name = "Register a new item", description = "Creates and registers a new item in the game.", examples = "register a new item with identifier \"copper\" named \"Copper\" with texture \"test:copper_item\"", pattern = "register [a] [new] item [with identifier] {string} [named {string}] [[and] with (texture {resource}|model {resource})] [with [max] stack size {number}] [in tab {string}]"),
                @Feature(name = "Register a new block", description = "Creates and registers a new block in the game.", examples = "register a new block with identifier \"copper_ore\" named \"Copper Ore\" with texture \"test:copper_ore\"", pattern = "register [a] [new] block [with identifier] {string} [named {string}] [[and] with (texture {resource}|model {resource})] [with [max] stack size {number}] [with hardness {number}] [with harvest level {number}] [with material {string}] [dropping {resource}] [in tab {string}]"),
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
        System.out.println("Registering new block");
        String identifier = (String) getParameter(1).get(context).getObject();
        String registryName = SqriptUtils.getIdentifier(identifier);
        String blockName = getParameterOrDefault(getParameter(2), null, context);
        ResourceLocation blockTexture = getParameterOrDefault(getParameter(3), null, context);
        ResourceLocation blockModel = getParameterOrDefault(getParameter(4), null, context);
        int maxStackSize = getParameterOrDefault(getParameter(5), 64, context);
        float hardness = getParameterOrDefault(getParameter(6), 0, context);
        int harvestLevel = getParameterOrDefault(getParameter(7), 0, context);
        String materialName = getParameterOrDefault(getParameter(8), "iron", context);
        ResourceLocation drop =  getParameterOrDefault(getParameter(9), null, context);
        CreativeTabs creativeTab = loadTabFromName(getParameterOrDefault(getParameter(10), "misc", context));

        Material material = Material.ROCK;
        fr.nico.sqript.forge.common.ScriptBlock.ScriptBlock block = new fr.nico.sqript.forge.common.ScriptBlock.ScriptBlock(material,drop,harvestLevel);
        block.setRegistryName(getLine().getScriptInstance().getName(),registryName);
        block.setHardness(hardness);
        block.setCreativeTab(creativeTab);

        //System.out.println("Max stack size of item is : "+maxStackSize);
        SqriptForge.blocks.add(block);

        String blockModelName = blockTexture == null ? blockModel.toString() : blockTexture.toString().replaceAll("\\.png","");
        String itemBlockModelName = blockTexture == null ? blockModel.getNamespace()+":block/"+blockModel.getPath() : blockTexture.toString().replaceAll("\\.png","");

        try {
            if(blockTexture!=null)
                createBlockModel(registryName, blockModelName);
            createBlockState(registryName,blockModelName);
            createItemBlockModel(registryName,itemBlockModelName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Item itemBlock = new ItemBlock(block){
            @Override
            public String getItemStackDisplayName(ItemStack stack) {
                return blockName == null ? identifier : blockName;
            }
        }.setRegistryName(block.getRegistryName());
        SqriptForge.items.add(itemBlock);
    }

    private void registerNewItem(ScriptContext context) throws ScriptException {
        System.out.println("Registering new item");
        String name = (String) getParameter(1).get(context).getObject();
        String displayName = getParameterOrDefault(getParameter(2), name, context);
        String registryName = SqriptUtils.getIdentifier(name);
        ResourceLocation itemTexture = getParameterOrDefault(getParameter(3), null, context);
        ResourceLocation itemModel = getParameterOrDefault(getParameter(4), null, context);
        int itemStackSize = getParameterOrDefault(getParameter(5), 64, context);
        CreativeTabs creativeTab = loadTabFromName(getParameterOrDefault(getParameter(6), "misc", context));

        Item item = new ScriptItemBase(displayName);
        item.setRegistryName(getLine().getScriptInstance().getName(), registryName);
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

    private CreativeTabs loadTabFromName(String tabName) {
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            try {
                Field tabLabelField = CreativeTabs.class.getDeclaredField("tabLabel");
                tabLabelField.setAccessible(true);
                String tabLabel = (String) tabLabelField.get(tab);
                if (tabLabel.equalsIgnoreCase(tabName) || tabName.startsWith(tabLabel))
                    return tab;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
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

    private void createBlockState(String registryName, String modelName) throws Exception {
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
                    +"        \"facing=north\":   { \"model\": \""+modelName+"\", \"y\":90 },\n"
                    +"        \"facing=east\":   { \"model\": \""+modelName+"\", \"y\":180 },\n"
                    +"        \"facing=south\":   { \"model\": \""+modelName+"\", \"y\":270 },\n"
                    +"        \"facing=west\":   { \"model\": \""+modelName+"\" }\n"
                    +"    }\n"
                    +"}"
            ).replaceAll("'","\"");
            createFileIfNotExist(jsonFile,content);
    }


    private void createItemBlockModel(String registryName, String modelName) throws Exception {
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
                    +"  'parent': '"+modelName+"',\n"
                    + "\"display\": {\r\n        \"gui\": {\r\n            \"rotation\": [ 30, 225, 0 ],\r\n            \"translation\": [ 0, 0, 0],\r\n            \"scale\":[ 0.625, 0.625, 0.625 ]\r\n        },\r\n        \"ground\": {\r\n            \"rotation\": [ 0, 0, 0 ],\r\n            \"translation\": [ 0, 3, 0],\r\n            \"scale\":[ 0.25, 0.25, 0.25 ]\r\n        },\r\n        \"fixed\": {\r\n            \"rotation\": [ 0, 0, 0 ],\r\n            \"translation\": [ 0, 0, 0],\r\n            \"scale\":[ 0.5, 0.5, 0.5 ]\r\n        },\r\n        \"thirdperson_righthand\": {\r\n            \"rotation\": [ 75, 45, 0 ],\r\n            \"translation\": [ 0, 2.5, 0],\r\n            \"scale\": [ 0.375, 0.375, 0.375 ]\r\n        },\r\n        \"firstperson_righthand\": {\r\n            \"rotation\": [ 0, 45, 0 ],\r\n            \"translation\": [ 0, 0, 0 ],\r\n            \"scale\": [ 0.40, 0.40, 0.40 ]\r\n        },\r\n        \"firstperson_lefthand\": {\r\n            \"rotation\": [ 0, 225, 0 ],\r\n            \"translation\": [ 0, 0, 0 ],\r\n            \"scale\": [ 0.40, 0.40, 0.40 ]\r\n        }\r\n    }"
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
