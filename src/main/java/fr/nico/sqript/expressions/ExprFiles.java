package fr.nico.sqript.expressions;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Expression(name = "File Expressions",
        features = {
                @Feature(name = "Content of text file", description = "Returns an array containing the lines of the given file. The root folder is /.minecraft/. Will create the file it it does not exist when setting the content.", examples = "content of file \"/scripts/my_script_config.txt\"", pattern = "content of [text] file {string}", type = "array"),
                @Feature(name = "Content of image file", description = "Returns the given file read as an image. The root folder is /.minecraft/. Will create the image if it does not exist when setting the content.", examples = "content of image \"/scripts/my_image.png\"", pattern = "content of image [file] {string}", type = "image"),
                @Feature(name = "New image", description = "Returns a new image sized with the given width and height.", examples = "new image with width 100 and height 450", pattern = "[a] [new] image with width {number} and height {number}", type = "image"),
                @Feature(name = "Image pixel", description = "Returns the color of an image's pixel. Setting a pixel will only apply the changes in the memory, not on any file. Use the expression \"content of image file\" to save the content on a file.", examples = "set {my_image}'s pixel at [10,20] to 0xFFFF0000 #Sets the pixel to a full red pixel", pattern = "{image}['s] pixel at {array}", type = "color"),
}
)
public class ExprFiles extends ScriptExpression {
    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                String file = parameters[0].getObject().toString();
                File f = new File(getLine().getScriptInstance().getScriptFile().getParentFile().getParentFile(),file);
                ArrayList<ScriptType<?>> array = new ArrayList<>();
                try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        array.add(new TypeString(line));
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("File not found : "+e.getMessage()+" ("+file+")");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new TypeArray(array);
            case 1:
                file = parameters[0].getObject().toString();
                f = new File(getLine().getScriptInstance().getScriptFile().getParentFile().getParentFile(),file);

                try {
                    BufferedImage image = ImageIO.read(f);
                    return new TypeImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case 2:
                int width = ((TypeNumber)parameters[0]).getObject().intValue();
                int height = ((TypeNumber)parameters[1]).getObject().intValue();
                return new TypeImage(new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB));
            case 3:
                BufferedImage image = ScriptManager.parse(parameters[0],TypeImage.class).getObject();
                Vec3d pixelPos = SqriptUtils.arrayToLocation(((TypeArray)parameters[1]).getObject());
                return new TypeColor(new Color(image.getRGB((int)pixelPos.x,(int)pixelPos.y)));

        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        //System.out.println("Called with "+ Arrays.toString(parameters)+" "+to+" "+getMatchedIndex());
        switch (getMatchedIndex()) {
            case 0:
                ArrayList<ScriptType<?>> content = ((TypeArray) to).getObject();
                String file = parameters[0].getObject().toString();
                File f = new File(getLine().getScriptInstance().getScriptFile().getParentFile().getParentFile(), file);
                PrintWriter pr = null;

                try {
                    pr = new PrintWriter(f);
                    for (ScriptType<?> scriptType : content) {
                        pr.println(scriptType.toString());
                    }
                    pr.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            case 1:
                TypeImage image = ScriptManager.parse(to,TypeImage.class);
                file = parameters[0].getObject().toString();
                f = new File(getLine().getScriptInstance().getScriptFile().getParentFile().getParentFile(), file);
                try {
                    ImageIO.write(image.getObject(), "png", f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case 3:

                Color color = (ScriptManager.parse(to,TypeColor.class)).getObject();
                //System.out.println("Color is : "+Integer.toHexString(color.getRGB())+" "+Integer.toHexString(color.getTransparency()));
                image = ScriptManager.parse(parameters[0],TypeImage.class);
                Vec3d pixelPos = SqriptUtils.arrayToLocation(((TypeArray)parameters[1]).getObject());
                image.getObject().setRGB((int)pixelPos.x,(int)pixelPos.y,color.getRGB());
                return true;
        }
        return false;
    }
}
