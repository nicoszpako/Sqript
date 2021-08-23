package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class EvtLiving {

    @Cancelable
    @Event(
            feature = @Feature(name = "Living damage",
                    description = "This event is triggered just before damage is applied to an entity.",
                    examples = "on living damage:",
                    pattern = "living damage"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the damage event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Damage amount", description = "The amount of dealt damage.", pattern = "[damage] amount", type = "number"),
                    @Feature(name = "Attacker", description = "The damage dealer of the damage event.", pattern = "attacker", type = "entity"),
            }
    )
    public static class EvtOnLivingDamage extends ScriptEvent {
        public EvtOnLivingDamage(Entity victim, DamageSource damageSource, float amount) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptTypeAccessor(new TypeNumber(amount),"amount"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Living death",
                    description = "This event is triggered just before an entity dies of damage.",
                    examples = "on living death:",
                    pattern = "living death"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the death event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
            }
    )
    public static class EvtOnLivingDeath extends ScriptEvent {
        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Living fall",
                    description = "This event is triggered as soon as an EntityLivingBase falls.",
                    examples = "on living fall:",
                    pattern = "[living] fall"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the damage event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Distance", description = "The distance fallen.", pattern = "damage distance", type = "number"),
                    @Feature(name = "Damage multiplier", description = "The damage multiplier of the fall event.", pattern = "damage multiplier", type = "number"),
            }
    )
    public static class EvtOnLivingFall extends ScriptEvent {
        public EvtOnLivingFall(Entity victim, float distance, float damageMultiplier) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(new TypeNumber(distance),"distance"), new ScriptTypeAccessor(new TypeNumber(damageMultiplier),"damageMultiplier"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Death drops",
                    description = "This event is triggered when the death of an entity causes the appearance of objects.",
                    examples = "on living drop of death:",
                    pattern = "(living) drop[s] of death"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the death event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
                    @Feature(name = "Drops", description = "An array of the items that are going to be dropped.", pattern = "drops", type = "array"),
            }
    )
    public static class EvtOnLivingDrops extends ScriptEvent {
        public EvtOnLivingDrops(Entity victim, DamageSource damageSource, TypeArray typeArray) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(),"victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(),"attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource),"damageType"), new ScriptTypeAccessor(typeArray,"drops"));
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Entity join World",
                    description = "This event is triggered when an entity is added to the world.",
                    examples = "on entity join world:",
                    pattern = "entity join world"),
            accessors = {
                    @Feature(name = "Entity", description = "The spawned entity.", pattern = "entity", type = "entity"),
                    @Feature(name = "World", description = "The instance of the world the entity has spawn.", pattern = "world", type = "world"),
            }
    )
    public static class EvtOnEntityJoinWorld extends ScriptEvent {
        public EvtOnEntityJoinWorld(Entity entity, World world) {
            super(new ScriptTypeAccessor(entity != null ? new TypeEntity(entity) : new TypeNull(),"entity"), new ScriptTypeAccessor(new TypeWorld(world),"world"));
        }
    }
}
