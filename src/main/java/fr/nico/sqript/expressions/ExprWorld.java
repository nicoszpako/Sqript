package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Expression(name = "Entities Expressions",
        features = {
                @Feature(name = "World", description = "Returns world.", examples = "world", pattern = "world", type = "world"),
                @Feature(name = "World time", description = "Returns the world time.", examples = "world time", pattern = "{world} time", type = "number"),
                @Feature(name = "World Entities BoundingBox", description = "Return list of entity entities in the boudingbox.", examples = "world", pattern = "{string} with in {axisalignedbb}", type = "array"),
        }
)
public class ExprWorld extends ScriptExpression {
    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                return new TypeWorld(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
            case 1:
                World world = (World) parameters[0].getObject();
                return new TypeNumber(world.getWorldTime());
            case 2:
                AxisAlignedBB axisAlignedBB = (AxisAlignedBB) parameters[1].getObject();
                world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
                String entityClassName = (String) parameters[0].getObject();
                try {
                    Class <? extends Entity > aClass = (Class< ? extends Entity >) Class.forName(entityClassName);
                    TypeArray typeArray = new TypeArray();
                    world.getEntitiesWithinAABB(aClass, axisAlignedBB).forEach(entity1 -> {
                        typeArray.add(new TypeEntity(entity1));
                    });
                    return typeArray;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return new TypeNull();
                }
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
