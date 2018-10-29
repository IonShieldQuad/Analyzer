package syntax;

import lexis.SymbolPack;

import java.util.*;


/**
 * Abstract class containing syntax rules
 */
public abstract class SyntaxPack {
    
    private Map<String, SyntaxSymbol> syntax = new HashMap<>();
    private String mainSymbol;
    private int identifierCode;
    private int literalCode;
    
    protected SyntaxPack() {
        init();
        addSyntaxSymbol("analyzer identifier", null, null);
        addSyntaxSymbol("analyzer literal", null, null);
    }
    
    protected abstract void init();
    
    public SyntaxSymbol getSyntaxSymbol(String name) {
        return this.syntax.get(name);
    }
    
    public Set<SyntaxSymbol> symbolSet() {
        Set<SyntaxSymbol> s = new HashSet<>();
        syntax.keySet().forEach((k) -> s.add(syntax.get(k)));
        return s;
    }
    
    public boolean hasSyntaxSymbol(String name) {
        return syntax.containsKey(name);
    }
    
    public void addSyntaxSymbol(String name, SyntaxOperation[][] patterns, String term) {
        new SyntaxSymbol(this, name, patterns, term);
    }
    
    public void addSyntaxSymbol(String name, SyntaxSymbol symbol) {
        if (this.syntax.containsKey(name)) {
            throw new IllegalArgumentException("Symbol name \"" + name + "\" already used");
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
    
    protected void addTerminalsFromPack(SymbolPack pack) {
        for (String s : pack.symbolSet()) {
            addSyntaxSymbol(s, null, Integer.toString(pack.find(s)));
        }
        setIdentifierCode(pack.getIdentifierCode());
        setLiteralCode(pack.getLiteralCode());
    }
    
    
    static class OperationPosition {
        SyntaxSymbol symbol;
        int patternIndex;
        int termNum = 0;
        int operationIndex;
        final Stack<SyntaxSymbol.LoopData> loops = new Stack<>();
        final Stack<SyntaxSymbol.SelectData> selects = new Stack<>();
        
        OperationPosition() {
            symbol = null;
            patternIndex = 0;
            operationIndex = 0;
        }
        
        OperationPosition(SyntaxSymbol symbol) {
            this.symbol = symbol;
            patternIndex = 0;
            operationIndex = 0;
        }
        
        OperationPosition(SyntaxSymbol symbol, int patternIndex, int operationIndex) {
            this.symbol = symbol;
            this.patternIndex = patternIndex;
            this.operationIndex = operationIndex;
        }
        
        SyntaxOperation find() {
            return symbol.getPatterns()[patternIndex][operationIndex];
        }
    }
}
