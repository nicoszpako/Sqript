package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

@Action(name = "Sound actions",
        features = {
                @Feature(name = "Play a sound at a specific location", description = "Plays a sound at a specific location.", examples = "play minecraft:block.anvil.fall at player's location", pattern = "play {+resource} at {location} [with pitch {number}] [[and] with volume {number}] [in category {string}]"),
                @Feature(name = "Play a sound", description = "Plays a sound.", examples = "play minecraft:block.anvil.fall to player", pattern = "play {resource} [with pitch {number}] [[and] with volume {number}]", side = Side.CLIENT),
                @Feature(name = "Stop all sounds", description = "Stop all playing sounds.", examples = "stop sounds", pattern = "stop [all] sounds", side = Side.CLIENT),
                @Feature(name = "Stop a specific sound", description = "Stop a specific sound for a player.", examples = "stop sound \"my_sound\"", pattern = "stop sound {string} [in category {string}]", side = Side.CLIENT)
        }
)
public class ActSounds extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                ILocatable locatable = (ILocatable) getParameter(2).get(context);
                String category = getParameterOrDefault(getParameter(5), "player", context);
                if(FMLCommonHandler.instance().getSide() != net.minecraftforge.fml.relauncher.Side.SERVER)
                    throw new ScriptException.ScriptBadSideException(this.getLine(), FMLCommonHandler.instance().getSide());
                FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playSound(null, locatable.getPos(), SoundEvent.REGISTRY.getObject((ResourceLocation) getParameter(1).get(context).getObject()), SoundCategory.getByName(category.toLowerCase(Locale.ROOT)),getParameterOrDefault(getParameter(4),100d, context).floatValue(),getParameterOrDefault(getParameter(3),1d, context).floatValue());
                break;
            case 1:
                //System.out.println("Playing sound");
                playClientSound(context);
                break;
            case 2:
                stopSounds();
                break;
            case 3:
                String name = (String) getParameter(1).get(context).getObject();
                category = getParameterOrDefault(getParameter(2), "player", context);
                stopSound(name,category);
        }
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    private void playClientSound(ScriptContext context) throws ScriptException {
        //System.out.println("Playing : "+(ResourceLocation) getParameter(1).get(context).getObject()+" with volume "+ getParameterOrDefault(getParameter(3),1d, context).floatValue()+ " with pitch "+getParameterOrDefault(getParameter(2),1d, context).floatValue());
        //System.out.println("A: "+(Minecraft.getMinecraft().getSoundHandler() == null));
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvent.REGISTRY.getObject((ResourceLocation) getParameter(1).get(context).getObject()), getParameterOrDefault(getParameter(2),1d, context).floatValue(),getParameterOrDefault(getParameter(3),1d, context).floatValue()));
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    private void stopSound(String name, String category) {
        Minecraft.getMinecraft().getSoundHandler().stop(name, SoundCategory.getByName(category.toLowerCase(Locale.ROOT)));
    }

    @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    private void stopSounds() {
        Minecraft.getMinecraft().getSoundHandler().stopSounds();
    }
}
