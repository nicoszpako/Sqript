package fr.nico.sqript.meta;

public class MatchResult {

    private int matchedIndex;
    private int matchStartPositionInString;
    private int marks;

    public MatchResult(int matchedIndex, int matchStartPositionInString, int marks) {
        this.matchedIndex = matchedIndex;
        this.matchStartPositionInString = matchStartPositionInString;
        this.marks = marks;
    }

    public int getMatchedIndex() {
        return matchedIndex;
    }

    public void setMatchedIndex(int matchedIndex) {
        this.matchedIndex = matchedIndex;
    }

    public int getMatchStartPositionInString() {
        return matchStartPositionInString;
    }

    public void setMatchStartPositionInString(int matchStartPositionInString) {
        this.matchStartPositionInString = matchStartPositionInString;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}
