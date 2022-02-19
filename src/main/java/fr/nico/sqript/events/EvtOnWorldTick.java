package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.TypeWorld;
import net.minecraft.world.World;

@Event(
        feature = @Feature(name = "World tick",
                description = "Called when the world ticks.",
                examples = "on world tick:",
                pattern = "world tick"),
        accessors = {
                @Feature(name = "World", description = "The world.", pattern = "world", type = "world"),
        }
)
public class EvtOnWorldTick extends ScriptEvent {

    public EvtOnWorldTick(World world) {
        super(new ScriptTypeAccessor(new TypeWorld(world),"world"));
    }

}
