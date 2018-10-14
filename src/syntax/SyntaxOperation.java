package syntax;

public class SyntaxOperation {
    private final String data;
    private final String[] params;

    SyntaxOperation(String data, String params) {
        this.data = data;
        if (params != null) {
            this.params = params.split(" ");
        }
        else {
            this.params = new String[0];
        }
    }

    String getData() {
        return data;
    }

    public String[] getParams() {
        return params.clone();
    }

    boolean isSymbol() {
        return contains("s");
    }

    boolean isIdentifier() {
        return contains("id");
    }

    boolean isLiteral() {
        return contains("lit");
    }

    boolean isLoopStart() {
        return contains("ls");
    }

    boolean isLoopEnd() {
        return contains("le");
    }

    boolean isSelectionStart() {
        return contains("ss");
    }

    boolean isSelectionEnd() {
        return contains("se");
    }

    boolean isSelectionBody() {
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
