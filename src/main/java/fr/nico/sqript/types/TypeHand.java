package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.io.File;

@Type(name = "hand", parsableAs = {})
public class TypeHand extends ScriptType< EnumHand > {

    public TypeHand(EnumHand enumHand) {
        super(enumHand);
    }

    @Override
    public String toString() {
        return this.getObject().name();
    }

    @Override
    public boolean equals(Object o) {
        return o.toString().equalsIgnoreCase(getObject().name());
    }

}
