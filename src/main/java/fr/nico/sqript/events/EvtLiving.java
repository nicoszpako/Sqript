package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.TypeArray;
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
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptTypeAccessor(new TypeNumber(amount),"amount"));
        }
    }

    @Cancelable
    @Event(name = "Living Death",
            description = "This event is triggered just before an entity dies of damage.",
            examples = "on living death:",
            patterns = "(living) death",
            accessors = {"victim:entity", "damageType:damage_source", "attacker:entity"}
    )
    public static class EvtOnLivingDeath extends ScriptEvent {
        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"));
        }
    }

    @Cancelable
    @Event(name = "Living Fall",
            description = "This event is triggered as soon as an EntityLivingBase falls.",
            examples = "on living fall:",
            patterns = "(living fall|fall)",
            accessors = {"victim:entity", "distance:number", "damageMultiplier:number"}
    )
    public static class EvtOnLivingFall extends ScriptEvent {
        public EvtOnLivingFall(Entity victim, float distance, float damageMultiplier) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(new TypeNumber(distance),"distance"), new ScriptTypeAccessor(new TypeNumber(damageMultiplier),"damageMultiplier"));
        }
    }

    @Cancelable
    @Event(name = "Living Drops",
            description = "This event is triggered when the death of an entity causes the appearance of objects.",
            examples = "on living drop of death:",
            patterns = "(living) drop[s] of death",
            accessors = {"victim:entity", "damageType:damage_source", "attacker:entity", "drops:array"}
    )
    public static class EvtOnLivingDrops extends ScriptEvent {
        public EvtOnLivingDrops(Entity victim, DamageSource damageSource, TypeArray typeArray) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptTypeAccessor(typeArray,"drops"));
        }
    }
}
