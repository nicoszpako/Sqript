package fr.nico.sqript.meta;

import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.structures.Side;

import java.util.regex.Pattern;

public class BlockDefinition {

    String name;
    String description;
    Pattern regex;
    String[] example;
    Side side;
    boolean reloadable;

    public Side getSide() {
        return side;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public Class<? extends ScriptBlock> getBlockClass() {
        return cls;
    }

    private Class<? extends ScriptBlock> cls;

    public BlockDefinition(String name, String description, String[] example, Class<? extends ScriptBlock> cls, String regex, Side side, boolean reloadable) {
        this.name = name;
        this.example = example;
        this.description = description;
        this.cls=cls;
        this.regex = Pattern.compile(regex);
        this.side = side;
        this.reloadable = reloadable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getExample() {
        return example;
    }

    public void setExample(String[] example) {
        this.example = example;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pattern getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = Pattern.compile(regex);
    }
}
