package fr.nico.sqript.meta;

import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.structures.Side;

import java.util.regex.Pattern;

public class BlockDefinition {

    Feature feature;
    Feature[] fields;
    boolean reloadable;
    Pattern regex;

    public boolean isReloadable() {
        return reloadable;
    }

    public Class<? extends ScriptBlock> getBlockClass() {
        return cls;
    }

    private Class<? extends ScriptBlock> cls;

    public BlockDefinition(Class<? extends ScriptBlock> cls, Feature feature, Feature[] fields, boolean reloadable) {
        this.cls=cls;
        this.feature = feature;
        this.fields = fields;
        this.regex = Pattern.compile(feature.regex());
        this.reloadable = reloadable;
    }

    public Pattern getRegex() {
        return regex;
    }


    public Feature getFeature() {
        return feature;
    }

    public Class<? extends ScriptBlock> getCls() {
        return cls;
    }


    public Feature[] getFields() {
        return fields;
    }
}
