package fr.nico.sqript.blocks;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.forge.SqriptForge;
import fr.nico.sqript.forge.common.item.ScriptItemBase;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import scala.Int;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


@Block(name = "item",
        description = "item blocks",
        examples = "item my_item:",
        regex = "^item .*",
        side = Side.BOTH,
        fields = {"name","texture","max stack size","creative tab","durability","tool type"},
        reloadable = false
)
public class ScriptBlockItem extends ScriptBlock {


    public ScriptBlockItem(ScriptLine head) throws ScriptException {
        super(head);
    }

    @Override
    protected void load() throws Exception {
        ScriptCompileGroup group = new ScriptCompileGroup();
        String registryName = ScriptDecoder.splitAtDoubleDot(getHead().text.replaceFirst("item\\s+/", ""))[0].split(" ")[1];
        String texture = "",model ="";
        int maxStackSize = 1;
        CreativeTabs tab = CreativeTabs.MISC;
        int durability;
        String toolType;
        String displayName;

        if(!fieldDefined("name"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(),"item","name");
        if(!fieldDefined("texture") && !fieldDefined("model"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(),"item","texture or model");

        displayName = getSubBlock("name").getRawContent();
        if(fieldDefined("texture"))
            texture = getSubBlock("texture").getRawContent().replaceAll("\\.png","");
        if(fieldDefined("model"))
            model = getSubBlock("model").getRawContent();
        if(fieldDefined("max stack size"))
            maxStackSize = Integer.parseInt(getSubBlock("max stack size").getRawContent());
        if(fieldDefined("creative tab"))
            tab = loadTabFromName(getSubBlock("creative tab").getRawContent());

        ScriptItemBase item = new ScriptItemBase(registryName,this.getHead().scriptInstance.getName(),displayName,model);
        item.setRegistryName(this.getHead().scriptInstance.getName(),registryName);
        item.setUnlocalizedName(registryName);
        System.out.println("Max stack size of item is : "+maxStackSize);
        item.setMaxStackSize(maxStackSize);
        item.setCreativeTab(tab);
        SqriptForge.items.add(item);


        //Creating the model/blockstate file if the model file was not specified
        System.out.println("Creating model file for item : "+registryName);
        if(!fieldDefined("model")){
            //File scriptFile
            File mainFolder = new File(ScriptManager.scriptDir,this.getHead().scriptInstance.getName());
            if(!mainFolder.exists())
                mainFolder.mkdir();
            File itemModelsFolder = new File(mainFolder,"/models/item");
            if(!itemModelsFolder.exists())
                itemModelsFolder.mkdirs();
            //Il faut créer le dossier et l'enregistrer à la main
            File jsonFile = new File(itemModelsFolder,registryName+".json");
            String content = ("{"+"\n"
                            +"  'parent': 'item/generated',"+"\n"
                            +"  'textures': {"+"\n"
                            +"      'layer0': '"+texture+"'"+"\n"
                            +"  }"+"\n"
                            +"}").replaceAll("'","\"");
            createFile(jsonFile,content);
            ScriptManager.loadResources(mainFolder);
        }
    }

    private CreativeTabs loadTabFromName(String creative_tab) {
        for(CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY){
            if(tab.getTabLabel().equalsIgnoreCase(creative_tab))
                return tab;
        }
        return null;
    }


    private void createFile(File file, String contents) throws Exception {

        if (file.exists()) {
            if (!file.delete())
                ScriptManager.log.error("Failed to delete json file : "+file);
        }
        System.out.println("Creating file : "+file.getAbsolutePath());
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(contents);
        out.close();
    }

}
