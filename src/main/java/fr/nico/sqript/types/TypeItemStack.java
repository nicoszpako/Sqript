package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.ItemStack;

@Type(name = "item",
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

    @Override
    public boolean equals(Object o) {
        //System.out.println("Comparing "+this+" with "+((ScriptType)(o)).getObject());
        if(o instanceof TypeItemStack){
            return ItemStack.areItemStacksEqual(((TypeItemStack)(o)).getObject(),getObject());
        }else if(o instanceof TypeResource){
            return ((TypeResource)(o)).getObject().equals(getObject().getItem().getRegistryName());
        }else if(o instanceof TypeItem){
            return this.getObject().getItem() == ((TypeItem)o).getObject();
        }
        return false;
    }

}
