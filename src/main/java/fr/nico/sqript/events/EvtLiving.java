package fr.nico.sqript.events;

import fr.nico.sqript.meta.Event;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class EvtLiving {

    @Cancelable
    @Event(
            feature = @Feature(name = "Living damage",
                    description = "This event is triggered on server side just before damage is applied to an entity.",
                    examples = "on pig damage:",
                    pattern = "((1;(living|entity))|(2;{entity|resource})) damage[d] [by {entity|resource}]"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the damage event.", pattern = "(victim|entity)", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Damage amount", description = "The amount of dealt damage.", pattern = "[damage] amount", type = "number"),
                    @Feature(name = "Attacker", description = "The damage dealer of the damage event.", pattern = "attacker", type = "entity"),
            }
    )
    public static class EvtOnLivingDamage extends ScriptEvent {
        Entity victim;
        Entity source;

        public EvtOnLivingDamage(Entity victim, DamageSource damageSource, float amount) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(), "(victim|entity)"),
                    new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(), "attacker"),
                    new ScriptTypeAccessor(new TypeDamageSource(damageSource), "damageType"),
                    new ScriptTypeAccessor(new TypeNumber(amount), "amount"));
            this.victim = victim;
            this.source = damageSource.getImmediateSource();
        }



        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            boolean result = true;
            if (parameters.length == 0 || parameters[0] == null)
                result = false;
            else if (parameters[0] instanceof TypeEntity) {
                result = victim.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                result = ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == victim.getClass();
            }

            if (parameters[1] == null)
                result = false;
            else if (parameters[1] instanceof TypeEntity && source != null) {
                result = result && source.getClass().isAssignableFrom(((TypeEntity) parameters[1]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource && source != null) {
                result = result && ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[1]).getObject()).getEntityClass() == source.getClass();
            }
            return result;
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Living death",
                    description = "This event is triggered just before an entity dies of damage.",
                    examples = {"on player death:","on zombie death:"},
                    pattern = "((1;(living|entity))|(2;player)|(3;{entity|resource}))['s] death"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the death event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
            }
    )
    public static class EvtOnLivingDeath extends ScriptEvent {
        Entity victim;

        public EvtOnLivingDeath(Entity victim, DamageSource damageSource) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(), "victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(), "attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource), "damageType"));
            this.victim = victim;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            //System.out.println("Checking for parameters :"+ Arrays.toString(parameters));
            //System.out.println("Marks are : "+Integer.toBinaryString(marks));
            if (parameters.length == 0 || parameters[0] == null || ((marks >> 1) & 1) == 1)
                return true;
            else if (((marks >> 2) & 1) == 1 && victim instanceof EntityPlayer){
                return true;
            }
            else if (parameters[0] instanceof TypeEntity) {
                return victim.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == victim.getClass();
            }
            return false;
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Living fall",
                    description = "This event is triggered as soon as an EntityLivingBase falls.",
                    examples = "on living fall:",
                    pattern = "((1;(living|entity))|(2;{entity|resource}))['s] fall"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the damage event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Distance", description = "The distance fallen.", pattern = "damage distance", type = "number"),
                    @Feature(name = "Damage multiplier", description = "The damage multiplier of the fall event.", pattern = "damage multiplier", type = "number"),
            }
    )
    public static class EvtOnLivingFall extends ScriptEvent {
        Entity victim;

        public EvtOnLivingFall(Entity victim, float distance, float damageMultiplier) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(), "victim"), new ScriptTypeAccessor(new TypeNumber(distance), "distance"), new ScriptTypeAccessor(new TypeNumber(damageMultiplier), "damageMultiplier"));
            this.victim = victim;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            if (parameters.length == 0 || parameters[0] == null)
                return true;
            else if (parameters[0] instanceof TypeEntity) {
                return victim.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == victim.getClass();
            }
            return false;
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Death drops",
                    description = "This event is triggered when the death of an entity causes the appearance of objects.",
                    examples = "on living drop of death:",
                    pattern = "((1;(living|entity))|(2;{entity|resource}))['s] drop[s] of death"),
            accessors = {
                    @Feature(name = "Victim", description = "The victim of the death event.", pattern = "victim", type = "entity"),
                    @Feature(name = "Damage type", description = "The damage type of dealt damage.", pattern = "damage type", type = "damage_source"),
                    @Feature(name = "Attacker", description = "The damage dealer of the death event.", pattern = "attacker", type = "entity"),
                    @Feature(name = "Drops", description = "An array of the items that are going to be dropped.", pattern = "drops", type = "array"),
            }
    )
    public static class EvtOnLivingDrops extends ScriptEvent {
        Entity victim;

        public EvtOnLivingDrops(Entity victim, DamageSource damageSource, TypeArray typeArray) {
            super(new ScriptTypeAccessor(victim != null ? new TypeEntity(victim) : new TypeNull(), "victim"), new ScriptTypeAccessor(damageSource.getImmediateSource() != null ? new TypeEntity(damageSource.getImmediateSource()) : new TypeNull(), "attacker"), new ScriptTypeAccessor(new TypeDamageSource(damageSource), "damageType"), new ScriptTypeAccessor(typeArray, "drops"));
            this.victim = victim;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            if (parameters.length == 0 || parameters[0] == null)
                return true;
            else if (parameters[0] instanceof TypeEntity) {
                return victim.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == victim.getClass();
            }
            return false;
        }
    }

    @Cancelable
    @Event(
            feature = @Feature(name = "Entity join World",
                    description = "This event is triggered when an entity is added to the world.",
                    examples = "on entity join world:",
                    pattern = "((1;(living|entity))|(2;{entity|resource})) join world"),
            accessors = {
                    @Feature(name = "Entity", description = "The spawned entity.", pattern = "entity", type = "entity"),
                    @Feature(name = "World", description = "The instance of the world the entity has spawn.", pattern = "world", type = "world"),
            }
    )
    public static class EvtOnEntityJoinWorld extends ScriptEvent {
        Entity entity;

        public EvtOnEntityJoinWorld(Entity entity, World world) {
            super(new ScriptTypeAccessor(entity != null ? new TypeEntity(entity) : new TypeNull(), "entity"), new ScriptTypeAccessor(new TypeWorld(world), "world"));
            this.entity = entity;
        }

        @Override
        public boolean check(ScriptType[] parameters, int marks) {
            if (parameters.length == 0 || parameters[0] == null)
                return true;
            else if (parameters[0] instanceof TypeEntity) {
                return entity.getClass().isAssignableFrom(((TypeEntity) parameters[0]).getObject().getClass());
            } else if (parameters[0] instanceof TypeResource) {
                return ForgeRegistries.ENTITIES.getValue((ResourceLocation) (parameters[0]).getObject()).getEntityClass() == entity.getClass();
            }
            return false;
        }
    }
}
