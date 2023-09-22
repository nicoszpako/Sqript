package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeEntity;
import fr.nico.sqript.types.TypeNBTTagCompound;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Entities Expressions",
        features = {
                @Feature(name = "All entities", description = "Returns an array of all entities.", examples = "all entities", pattern = "all entities", type = "array"),
                @Feature(name = "Entity Name", description = "Returns the name of entity", examples = "entity's name", pattern = "{entity}['s] name", type = "string"),
                @Feature(name = "Entity ID", description = "Returns the id of entity.", examples = "id of entity", pattern = "id of {entity}", type = "number", priority = -1),
                @Feature(name = "Entity NBT", description = "Returns the nbt tag compound of entity", examples = "entity's nbt", pattern = "{entity}['s] nbt", type = "nbttagcompound"),
}
)
public class ExprEntities extends ScriptExpression {
    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                synchronized (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()) {
                    TypeArray a = new TypeArray();
                    for (Entity entity : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getLoadedEntityList()) {
                        a.getObject().add(new TypeEntity(entity));
                    }
                    return a;
                }
            case 1:
                Entity entity = (Entity) parameters[0].getObject();
                return new TypeString(entity.getName());
            case 2:
                entity = (Entity) parameters[0].getObject();
                return new TypeNumber(entity.getEntityId());
            case 3:
                entity = (Entity) parameters[0].getObject();
                return new TypeNBTTagCompound(entity.writeToNBT(new NBTTagCompound()));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
