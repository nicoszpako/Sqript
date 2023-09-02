package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeKeyBind;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Expression(name = "KeyInput Expressions",
        features = {
                @Feature(name = "Key binding register", description = "Register Custom KeyBinding", examples = "register key \"R\" with displayName \"test\" and with categoryName \"default\"", pattern = "register key {string} [with displayName {string}] and [with categoryName {string}]", type = "key", side = Side.CLIENT),
                @Feature(name = "Key binding pressed", description = "Triggered when the key is pressed", examples = "{key} is pressed", pattern = "{key} is pressed", type = "boolean", side = Side.CLIENT),
                @Feature(name = "Key binding down", description = "Triggered when the key is down", examples = "{key} is down", pattern = "{key} is keydown", type = "boolean", side = Side.CLIENT),
                @Feature(name = "Key binding from description", description = "Returns a key binding from a string.", examples = "key binding \"key.forward\"", pattern = "key [binding] {string}", type = "key", side = Side.CLIENT),
                @Feature(name = "Key code pressed", description = "Triggered when the given key code is pressed", examples = "42 code is pressed", pattern = "{number} code is pressed", type = "boolean", side = Side.CLIENT),
        }
)
public class ExprKeyInput extends ScriptExpression {

    @Override
    public ScriptType<?> get(ScriptContext context, ScriptType<?>[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                KeyBinding keyBinding = new KeyBinding((String) parameters[1].getObject(), Keyboard.getKeyIndex((String) parameters[0].getObject()), (String) parameters[2].getObject());
                ClientRegistry.registerKeyBinding(keyBinding);
                return new TypeKeyBind(keyBinding);
            case 1:
                return new TypeBoolean(((KeyBinding)parameters[0].getObject()).isPressed());
            case 2:
                return new TypeBoolean(((KeyBinding)parameters[0].getObject()).isKeyDown());
            case 3:
                String keyName = (String) parameters[0].getObject();
                List<KeyBinding> l = Arrays.stream(Minecraft.getMinecraft().gameSettings.keyBindings).filter(a->a.getKeyDescription().equalsIgnoreCase(keyName)).collect(Collectors.toList());
                if(!l.isEmpty() && l.get(0) != null)
                    return new TypeKeyBind(l.get(0));
                else return new TypeNull();
            case 4:
                return new TypeBoolean(Keyboard.isKeyDown((Integer)parameters[0].getObject()));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
