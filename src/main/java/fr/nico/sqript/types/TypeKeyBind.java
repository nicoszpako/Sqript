package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.File;

@Type(name = "key",
        parsableAs = {})
public class TypeKeyBind extends ScriptType<KeyBinding> {

    @Override
    public String toString() {
        return this.getObject().getDisplayName();
    }

    public TypeKeyBind(KeyBinding key) {
        super(key);
    }

}
