package fr.nico.sqript.meta;

public class MatchResult {

    private int matchedIndex;
    private int marks;

    public MatchResult(int matchedIndex, int marks) {
        this.matchedIndex = matchedIndex;
        this.marks = marks;
    }

    public int getMatchedIndex() {
        return matchedIndex;
    }

    public void setMatchedIndex(int matchedIndex) {
        this.matchedIndex = matchedIndex;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "n°"+getMatchedIndex()+":"+getMarks();
    }
}
