package lexis;

public class UnmatchedSubstringException extends Exception {
    private final String substring;
    public String getUnmatchedSubstring() {
        return this.substring;
    }

    UnmatchedSubstringException(String substring) {
        this.substring = substring;
    }
}
