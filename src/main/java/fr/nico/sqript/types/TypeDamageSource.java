package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

@Type(name = "damage_source",
        parsableAs = {})
public class TypeDamageSource extends ScriptType<DamageSource>
{
    public TypeDamageSource(DamageSource damageSource) {
        super(damageSource);
    }

    @Override
    public String toString() {
        return this.getObject().getDamageType();
    }

    @Override
    public boolean equals(Object o) {
        return o.toString().equalsIgnoreCase(getObject().getDamageType());
    }

}
