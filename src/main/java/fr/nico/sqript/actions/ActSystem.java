package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Action(name = "Stystem actions",
        features = @Feature(name = "Exit/Quit", description = "Quit the game.", examples = "quit the game", pattern = "quit [the] [game]")
)
public class ActSystem extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()){
            case 0:
                FMLCommonHandler.instance().handleExit(0);
                break;
        }
    }
}
