package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Items Expressions",
        features = @Feature(name = "Item NBT tag", description = "Returns the NBT tag of the given item.", examples = "item's nbt", pattern = "{item}'s nbt [tag]", type = "nbt")
)
public class ExprItems extends ScriptExpression {

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
