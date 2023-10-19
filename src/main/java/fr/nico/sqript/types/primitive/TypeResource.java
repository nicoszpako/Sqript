package fr.nico.sqript.types.primitive;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.types.TypeImage;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.interfaces.ISerialisable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Primitive(name = "resource",
        parsableAs = {},
        pattern = "(\\w+:[\\w./]+)"
)
public class TypeResource extends PrimitiveType<ResourceLocation> implements ISerialisable {

    static {
        ScriptManager.registerTypeParser(TypeString.class, TypeResource.class, a->new TypeResource(new ResourceLocation(a.getObject())), 0);
        ScriptManager.registerTypeParser(TypeItem.class, TypeResource.class, a->new TypeResource((a.getObject().getRegistryName())), 0);
        ScriptManager.registerTypeParser(TypeResource.class, TypeImage.class, a->{
            try {
                InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(a.getObject()).getInputStream();
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                return new TypeImage(bufferedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        },0);

    }




    public TypeResource(ResourceLocation parameter){
        super(parameter);
    }

    public TypeResource(String parameter){
        super(new ResourceLocation(arrange(parameter)));
    }

    public static String arrange(String parameter){
        return parameter.split(":")[0]+":"+((parameter.split(":")[1].endsWith(" png ") && !parameter.split(":")[1].startsWith("textures/"))?"textures/"+parameter.split(":")[1]:parameter.split(":")[1]);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setString("resource", this.getObject().toString());
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) throws ScriptException {
        this.setObject(new ResourceLocation(compound.getString("resource")));
    }
}
