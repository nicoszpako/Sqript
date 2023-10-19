package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

@Expression(name = "System Expressions",
        features = {
            @Feature(name = "Execution side", description = "Returns the current execution side.", examples = "execution side", pattern = "[execution] side", type = "string"),
            @Feature(name = "Window title", description = "Returns the current window title.", examples = "set the window title to \"Sqript\"", pattern = "window (name|title)", type = "string"),
            @Feature(name = "Window icons", description = "Returns the current window icons.", examples = "set window icons to [sample:favicon_wb_16.png,sample:favicon_wb_32.png]", pattern = "window icons", type = "array"),
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
                    InputStream i16 = new FileInputStream(new File(ScriptManager.scriptDir,l16.getNamespace()+"/"+l16.getPath()));
                    InputStream i32 = new FileInputStream(new File(ScriptManager.scriptDir,l32.getNamespace()+"/"+l32.getPath()));
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
