package syntax;

public class SyntaxOperation {
    private final String data;
    private final String[] params;

    public SyntaxOperation(String data, String params) {
        this.data = data;
        this.params = params.split(" ");
    }

    public String getData() {
        return data;
    }

    public String[] getParams() {
        return params;
    }

    public boolean isSymbol() {
        return contains("s");
    }

    public boolean isIdentifier() {
        return contains("id");
    }

    public boolean isLiteral() {
        return contains("lit");
    }

    public boolean isLoopStart() {
        return contains("ls");
    }

    public boolean isLoopEnd() {
        return contains("le");
    }

    public boolean isSelectionStart() {
        return contains("ss");
    }

    public boolean isSelectionEnd() {
        return contains("se");
    }

    public boolean isSelectionBody() {
        return contains("sb");
    }
    
    private boolean contains(String param) {
        for (String p : params) {
            if (p.equals(param)) {
                return true;
            }
        }
        return false;
    }
}
