package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.Item;

@Type(name = "itemtype",
        parsableAs = {TypeResource.class})
public class TypeItem extends ScriptType<Item> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        if(typeName.equalsIgnoreCase("resource")){
            return new TypeResource(getObject().getRegistryName());
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getRegistryName().toString();
    }

    public TypeItem(Item item) {
        super(item);
    }

    @Override
    public boolean equals(Object o) {
        return getObject() == ((TypeItem)(o)).getObject();
    }

}
