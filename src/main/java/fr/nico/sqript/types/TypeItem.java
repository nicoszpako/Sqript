package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.Item;
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

    @Override
    public boolean equals(Object o) {
        //System.out.println("Comparing "+this+" with "+((ScriptType)(o)).getObject());
        if(o instanceof TypeItem){
            return ItemStack.areItemStacksEqual(((TypeItem)(o)).getObject(),getObject());
        }else if(o instanceof TypeResource){
            return ((TypeResource)(o)).getObject().equals(getObject().getItem().getRegistryName());
        }else if(o instanceof TypeItemData){
            return this.getObject().getItem() == ((TypeItemData)o).getObject();
        }
        return false;
    }

}
