package fr.nico.sqript.structures;

public class TypeParserDefinition {

    private final int priority;
    private ITypeParser parser;

    public TypeParserDefinition(int priority, ITypeParser parser) {
        this.priority = priority;
        this.parser = parser;
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

}
