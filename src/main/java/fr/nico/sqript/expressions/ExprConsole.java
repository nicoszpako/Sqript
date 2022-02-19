package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Console Expressions",
        features = @Feature(name = "Console/server", description = "Returns the instance of the console/server.", examples = "server", pattern = "(console|server)", type = "sender")
)
public class ExprConsole extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                return new TypeConsole(FMLCommonHandler.instance().getMinecraftServerInstance());

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
