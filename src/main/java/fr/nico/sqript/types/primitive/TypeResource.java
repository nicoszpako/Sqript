package fr.nico.sqript.types.primitive;

import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.structures.ScriptElement;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@Primitive(name = "resource",
        parsableAs = {},
        pattern = "(\\w+:[\\w./]+)"
)
public class TypeResource extends PrimitiveType<ResourceLocation> {


    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

    public TypeResource(ResourceLocation parameter){
        super(parameter);
    }

    public TypeResource(String parameter){
        super(new ResourceLocation(parameter.split(":")[0],(parameter.split(":")[1].endsWith("png") && !parameter.split(":")[1].startsWith("textures/"))?"textures/"+parameter.split(":")[1]:parameter.split(":")[1]));
    }

}
