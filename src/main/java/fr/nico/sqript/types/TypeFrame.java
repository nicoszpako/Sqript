package fr.nico.sqript.types;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.forge.gui.Frame;
import fr.nico.sqript.forge.gui.Widget;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;

@Type(name = "frame",
        parsableAs = {})
public class TypeFrame extends ScriptType<Frame> {

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
                    //System.out.println("Adding "+b.getObject().toString()+" to "+frame.getObject().toString()+" "+((b.getObject() instanceof Widget)));
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
