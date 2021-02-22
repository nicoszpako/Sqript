package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

@Type(name = "damage_source",
        parsableAs = {})
public class TypeDamageSource extends ScriptType<DamageSource>
{
    public TypeDamageSource(DamageSource object) {
        super(object);
    }


    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null; //TODO USEFUL
    }
}
