package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.TypeKey;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;

@Expression(name = "KeyInput Expressions",
        description = "Manipulate the KeyInput",
        examples = "register key \"R\" with displayName \"test\" and with categoryName \"default\"",
        patterns = {
                "register key {string} [with displayName {string}] and [with categoryName {string}]:key",
                "{key} is pressed:boolean",
                "{key} is keydown:boolean"
        },
        side = Side.CLIENT
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
