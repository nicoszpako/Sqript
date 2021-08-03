package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptAccessor;
import fr.nico.sqript.types.TypeDamageSource;
import fr.nico.sqript.types.TypeEntity;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtLiving {

    @Cancelable
    @Event(name = "Living Damage",
            description = "This event is triggered just before damage is applied to an entity.",
            examples = "on living damage:",
            patterns = "living (damage)",
            accessors = {"victim:entity", "damageType:damage_source", "amount:number", "attacker:entity"}
    )
    public static class EvtOnLivingDamage extends ScriptEvent {
        public EvtOnLivingDamage(Entity victim, DamageSource damageSource, float amount) {
            super(victim != null ? new ScriptAccessor(new TypeEntity(victim),"victim") : new ScriptAccessor(new TypeNull(),"victim"), damageSource.getImmediateSource() != null ? new ScriptAccessor(new TypeEntity(damageSource.getImmediateSource()),"attacker") : new ScriptAccessor(new TypeNull(),"attacker"), new ScriptAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptAccessor(new TypeNumber(amount),"amount"));
        }
    }
}
