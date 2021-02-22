package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.item.ItemStack;

import java.io.File;

@Type(name = "itemstack",
        parsableAs = {})
public class TypeItemStack extends ScriptType<ItemStack> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getDisplayName();
    }

    public TypeItemStack(ItemStack itemStack) {
        super(itemStack);
    }


}
