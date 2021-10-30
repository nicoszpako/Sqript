package fr.nico.sqript.structures;

public class TypeParserDefinition {

    private final int priority;
    private ITypeParser parser;
    private final Class from;
    private final Class to;

    public TypeParserDefinition(int priority, ITypeParser parser, Class from, Class to) {
        this.priority = priority;
        this.parser = parser;
        this.from = from;
        this.to = to;
    }

    public int getPriority() {
        return priority;
    }

    public ITypeParser getParser() {
        return parser;
    }

    public void setParser(ITypeParser parser) {
        this.parser = parser;
    }

    public Class getFrom() {
        return from;
    }

    public Class getTo() {
        return to;
    }
}
