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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

@Action(name = "Sound actions",
        features = @Feature(name = "Play sound to a specific player", description = "Plays a sound to a specific player, other players won't hear it.", examples = "play minecraft:block.anvil.fall to player", pattern = "play {resource} to {player}", side = Side.CLIENT)
)
public class ActSounds extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                EntityPlayer player = (EntityPlayer) getParameter(2).get(context).getObject();
                player.playSound(SoundEvent.REGISTRY.getObject((ResourceLocation) getParameter(1).get(context).getObject()),100,1);
                break;
        }
    }
}
