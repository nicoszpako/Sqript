package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeKey;
import fr.nico.sqript.types.primitive.TypeBoolean;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

@Expression(name = "KeyInput Expressions",
        features = {
                @Feature(name = "KeyBinding Register", description = "Register Custom KeyBinding", examples = "register key \"R\" with displayName \"test\" and with categoryName \"default\"", pattern = "register key {string} [with displayName {string}] and [with categoryName {string}]", type = "key", side = Side.CLIENT),
                @Feature(name = "KeyBinding Pressed", description = "triggered when the key is pressed", examples = "{key} is pressed", pattern = "{key} is pressed", type = "boolean", side = Side.CLIENT),
                @Feature(name = "KeyBinding Down", description = "triggered when the key is down", examples = "{key} is down", pattern = "{key} is keydown", type = "boolean", side = Side.CLIENT),
        }
)
public class ExprKeyInput extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                KeyBinding keyBinding = new KeyBinding((String) parameters[1].getObject(), Keyboard.getKeyIndex((String) parameters[0].getObject()), (String) parameters[2].getObject());
                ClientRegistry.registerKeyBinding(keyBinding);
                return new TypeKey(keyBinding);
            case 1:
                return new TypeBoolean(((KeyBinding)parameters[0].getObject()).isPressed());
            case 2:
                return new TypeBoolean(((KeyBinding)parameters[0].getObject()).isKeyDown());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
