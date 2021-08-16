package fr.nico.sqript.meta;

import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.structures.Side;

import java.util.regex.Pattern;

public class LoopDefinition {

    private final Pattern pattern;
    private final Class<? extends ScriptLoop> cls;
    private final Side side;
    private final String name;
    private final int priority;

    public int getPriority() {
        return priority;
    }

    public LoopDefinition(String pattern, Class cls, Side side, String name,int priority){
        this.pattern = Pattern.compile(pattern);
        this.cls = cls;
        this.side = side;
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Side getSide() {
        return side;
    }

    public Class<? extends ScriptLoop> getLoopClass() {
        return cls;
    }

    public boolean matches(String line){
        return pattern.matcher(line).matches();
    }

}
