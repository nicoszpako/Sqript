package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

@Expression(name = "Strings Expressions",
        description = "Manipulate strings",
        examples = "length of \"test\"",
        patterns = {
            "length of {string}:number",
            "substring of {string} from {number} to {number}:string",
            "{string} split at each {string}:array",
            "character at [position] {number} of {string}:{string}",
        },
        side = Side.CLIENT
)
public class ExprStrings extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch(getMatchedIndex()){
            case 0:
                TypeString string = (TypeString) parameters[0];
                return new TypeNumber(string.getObject().length());
            case 1:
                string = (TypeString) parameters[0];
                TypeNumber start = (TypeNumber) parameters[1];
                TypeNumber end = (TypeNumber) parameters[2];
                return new TypeString(string.getObject().substring(start.getObject().intValue(),end.getObject().intValue()));
            case 2:
                string = (TypeString) parameters[0];
                TypeString split = (TypeString) parameters[1];
                ArrayList splits = new ArrayList();
                for(String s : string.getObject().split(split.getObject())){
                    splits.add(new TypeString(s));
                }
                return new TypeArray(splits);
            case 3:
                TypeNumber index = (TypeNumber) parameters[0];
                string = (TypeString) parameters[1];
                return new TypeString(String.valueOf(string.getObject().charAt(index.getObject().intValue())));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
