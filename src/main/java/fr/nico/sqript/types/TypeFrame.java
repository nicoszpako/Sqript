package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.forge.gui.Frame;
import fr.nico.sqript.forge.gui.Widget;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.types.interfaces.ILocatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

@Type(name = "frame",
        parsableAs = {})
public class TypeFrame extends ScriptType<Frame> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return "frame";
    }

    public TypeFrame(Frame frame) {
        super(frame);
    }

    static {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeFrame.class, ScriptElement.class, TypeFrame.class,
                (a,b) -> {
                    TypeFrame frame = (TypeFrame)a;
                    if (b.getObject() instanceof Widget){
                        Widget widget = ((Widget)b.getObject());
                        frame.getObject().addWidget(widget);
                        return frame;
                    }else{
                        return null;
                    }

                },2);
        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeFrame.class, ScriptElement.class, TypeFrame.class,
                (a,b) -> {
                    TypeFrame frame = (TypeFrame)a;
                    if (b.getObject() instanceof Widget){
                        Widget widget = ((Widget)b.getObject());
                        frame.getObject().removeWidget(widget);
                        return frame;
                    }else{
                        return null;
                    }
                },2);
    }


}
