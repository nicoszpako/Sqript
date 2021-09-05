package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Type(name = "itemdata",
        parsableAs = {})
public class TypeItemData extends ScriptType<Item> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getRegistryName().toString();
    }

    public TypeItemData(Item item) {
        super(item);
    }

    @Override
    public boolean equals(Object o) {
        return getObject() == ((TypeItemData)(o)).getObject();
    }

}
