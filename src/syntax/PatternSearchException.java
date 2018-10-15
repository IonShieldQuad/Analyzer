package syntax;

public class PatternSearchException extends Exception {
    private final String name;
    private final SyntaxOperation[] pattern;
    private final int index;
    private final String data;

    public PatternSearchException(String name, SyntaxOperation[] pattern, int index, String data) {
        this.name = name;
        this.pattern = pattern;
        this.data = data;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public SyntaxOperation[] getPattern() {
        return pattern;
    }

    public String getData() {
        return data;
    }
    
    public int getIndex() {
        return index;
    }
}
