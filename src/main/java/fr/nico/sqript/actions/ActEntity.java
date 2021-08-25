package fr.nico.sqript.actions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeEntity;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.interfaces.ILocatable;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Objects;

@Action(name = "Entity Actions",
        features = {
            @Feature(name = "Spawn entity", description = "Spawns an entity at the given location.", examples = "spawn a zombie at player's location", pattern = "spawn (a|{number}) {entity|resource} at {location}"),
            @Feature(name = "Kill entity", description = "Kills the given entity.", examples = "kill player", pattern = "kill {entity}"),
        }
)
public class ActEntity extends ScriptAction {

    @SideOnly(Side.SERVER)
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        switch (getMatchedIndex()) {
            case 0:
                double number = getParameterOrDefault(getParameter(1), 1d, context);
                ScriptType entityType = getParameters().get(1).get(context);
                ILocatable pos = (ILocatable) getParameters().get(2).get(context);
                Entity entity = null;
                World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
                for (int i = 0; i < number; i++) {
                    if(entityType instanceof TypeEntity)
                        entity = ((TypeEntity)entityType).getObject();
                    else if(entityType instanceof TypeResource){
                        entity = ForgeRegistries.ENTITIES.getValue(((TypeResource)entityType).getObject()).newInstance(world);
                    }
                    if (entity != null) {
                        entity.setLocationAndAngles(pos.getVector().x, pos.getVector().y, pos.getVector().z, 0, 0);
                        world.spawnEntity(entity);
                    }
                }
                return;
            case 1:
                entity = (Entity) getParameters().get(0).get(context).getObject();
                entity.setDead();
                return;
        }
    }
}