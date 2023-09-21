package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;

@Type(name = "itemtype",
        parsableAs = {TypeResource.class})
public class TypeItem extends ScriptType<Item> {

    static {
        ScriptManager.registerTypeParser(TypeResource.class,TypeItem.class, r-> {
            //System.out.println(r.getObject());
            return new TypeItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(r.getObject())));
        },0);
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
