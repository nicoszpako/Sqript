package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.File;

@Type(name = "item",
        parsableAs = {})
public class TypeItem extends ScriptType<ItemStack> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getDisplayName();
    }

    public TypeItem(ItemStack itemStack) {
        super(itemStack);
    }


}
