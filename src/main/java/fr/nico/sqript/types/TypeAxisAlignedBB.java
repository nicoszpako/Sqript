package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.UUID;

@Type(name = "axisalignedbb",
        parsableAs = {}
)
public class TypeAxisAlignedBB extends ScriptType< AxisAlignedBB > {

    public TypeAxisAlignedBB(AxisAlignedBB object) {
        super(object);
    }

    @Override
    public String toString() {
        return getObject().toString();
    }


}
