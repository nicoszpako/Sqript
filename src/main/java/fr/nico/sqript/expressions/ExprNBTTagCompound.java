package fr.nico.sqript.expressions;

import fr.nico.sqript.SqriptUtils;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.TypeNBTTagCompound;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

@Expression(name = "NBTTagCompound",
        features = {
                @Feature(name = "NBTTagCompound", description = "Returns a new NBTTagCompound.", examples = "set {my_nbtcompound} to a new nbt compound", pattern = "[a] [new] nbt compound", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound from a json string", description = "Returns a new NBTTagCompound based on elements of a json string.", examples = "set {my_nbtcompound} to a new nbt compound with json \"{\"name\":\"test\"}\"", pattern = "[a] [new] nbt [tag] [compound] (with|from|of) json [string] {string}", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound from a dictionary", description = "Returns a new NBTTagCompound based on elements of a dictionary.", examples = "set {my_nbtcompound} to nbt from {dict}", pattern = "[a] [new] nbt [tag] [compound] from [dictionary] {dictionary}", type = "nbttagcompound"),
                @Feature(name = "NBTTagCompound value", description = "Returns value of key NBTTagCompound.", examples = "tag {string} of {nbt}", pattern = "tag {string} of {nbttagcompound}"),
        }
)
public class ExprNBTTagCompound extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                return new TypeNBTTagCompound(new NBTTagCompound());
            case 1:
                TypeString s = (TypeString) parameters[0];
                try {
                    return new TypeNBTTagCompound(JsonToNBT.getTagFromJson(s.getObject()));
                } catch (NBTException e) {
                    e.printStackTrace();
                    return new TypeNull();
                }
            case 2:
                TypeDictionary dictionary = (TypeDictionary) parameters[0];
                return new TypeNBTTagCompound(dictionary.write(new NBTTagCompound()));
            case 3:
                TypeNBTTagCompound tagCompound = (TypeNBTTagCompound) parameters[1];
                return SqriptUtils.getTagFromTypeNBTTagCompound(tagCompound.getObject(), ((TypeString) parameters[0]).getObject());
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 3:
                String key = (String) parameters[0].getObject();
                NBTTagCompound nbtTagCompound = (NBTTagCompound) parameters[1].getObject();
                SqriptUtils.setTag(nbtTagCompound, key, to);
                return true;
        }
        return false;
    }
}
