package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeConsole;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.Display;
import sun.net.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

@Expression(name = "System Expressions",
        description = "Get informations about the system",
        examples = "execution side",
        patterns = {
            "[execution] side:string",
            "window (name|title):string",
            "window icons:array"
        }
)
public class ExprSystem extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {

        switch(getMatchedIndex()){
            case 0:
                return new TypeString(FMLCommonHandler.instance().getSide().toString());
            case 1:
                return new TypeString(Display.getTitle());
            case 2:
                return null;
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                return false;
            case 1:
                Display.setTitle(to.getObject().toString());
                return true;
            case 2:
                TypeArray array = (TypeArray) to;
                ResourceLocation l16 = (ResourceLocation) array.getObject().get(0).getObject();
                ResourceLocation l32 = (ResourceLocation) array.getObject().get(1).getObject();
                try {
                    InputStream i16 = new FileInputStream(new File(ScriptManager.scriptDir,l16.getResourceDomain()+"/"+l16.getResourcePath()));
                    InputStream i32 = new FileInputStream(new File(ScriptManager.scriptDir,l32.getResourceDomain()+"/"+l32.getResourcePath()));
                    Display.setIcon(new ByteBuffer[]{loadIcon(i16), loadIcon(i32)});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }

    private static ByteBuffer loadIcon(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[]) null, 0,
                bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

}
