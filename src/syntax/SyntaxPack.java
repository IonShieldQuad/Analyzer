package syntax;

import java.util.HashMap;
import java.util.Map;


/**
 * Abstract class
 */
public abstract class SyntaxPack {
    
    private Map<String, SyntaxSymbol> syntax = new HashMap<>();
    private String mainSymbol;
    private int identifierCode;
    private int literalCode;
    
    SyntaxPack() {
        init();
    }
    
    protected abstract void init();
    
    public SyntaxSymbol getSyntaxSymbol(String name) {
        return this.syntax.get(name);
    }
    
    public boolean hasSyntaxSymbol(String name) {
        return syntax.containsKey(name);
    }
    
    public void addSyntaxSymbol(String name, SyntaxOperation[][] patterns, String term) {
        new SyntaxSymbol(this, name, patterns, term);
    }
    
    public void addSyntaxSymbol(String name, SyntaxSymbol symbol) {
        if (this.syntax.containsKey(name)) {
            throw new IllegalArgumentException("Symbol name already used");
        }
        this.syntax.put(name, symbol);
    }
    
    public int getIdentifierCode() {
        return identifierCode;
    }
    
    public int getLiteralCode() {
        return literalCode;
    }
    
    public void setIdentifierCode(int identifierCode) {
        this.identifierCode = identifierCode;
    }
    
    public void setLiteralCode(int literalCode) {
        this.literalCode = literalCode;
    }
    
    public String getMainSymbol() {
        return mainSymbol;
    }
    
    protected void setMainSymbol(String mainSymbol) {
        this.mainSymbol = mainSymbol;
    }
}
