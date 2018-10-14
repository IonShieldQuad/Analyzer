package syntax;

public class SyntaxError {
    private final String message;
    private final String symbol;
    private final int index;
    
    SyntaxError(String message, String symbol, int index) {
    
        this.message = message;
        this.symbol = symbol;
        this.index = index;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public int getIndex() {
        return index;
    }
}
