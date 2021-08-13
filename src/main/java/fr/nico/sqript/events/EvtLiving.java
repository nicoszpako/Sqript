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
            super(new ScriptAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptAccessor(new TypeNumber(amount),"amount"));
        }
    }

    @Cancelable
    @Event(name = "Living Death",
            description = "This event is triggered just before an entity dies of damage.",
            examples = "on living death:",
            patterns = "living (death)",
            accessors = {"victim:entity", "damageType:damage_source", "attacker:entity"}
    )
    public static class EvtOnLivingDeath extends ScriptEvent {
        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptAccessor(new TypeDamageSource(damageSource),"damageType"));
        }
    }
}
