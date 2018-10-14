package core;

import lexis.SymbolPack;
import syntax.OperationResult;
import syntax.PatternSearchException;
import syntax.SyntaxPack;
import syntax.SyntaxSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SymbolsDict {

    private SymbolPack symbols;
    private SyntaxPack syntax;
    private Map<String, Integer> idMap = new HashMap<>();

    public SymbolsDict(SymbolPack symbolPack, SyntaxPack syntaxPack) {
        this.symbols = symbolPack;
        this.syntax = syntaxPack;
        
        if (syntaxPack != null) {
            
            for (String name : symbols.symbolSet()) {
                syntaxPack.addSyntaxSymbol(name, null, Integer.toString(symbols.find(name)));
            }
            
            syntaxPack.setIdentifierCode(symbolPack.getIdentifierCode());
            syntaxPack.setLiteralCode(symbolPack.getLiteralCode());
        }
    }

    /**Finds and returns a constant symbol from map*/
    public int getSymbol(String symbol) {
        return this.symbols.find(symbol);
    }
    
    public String getSymbol(int symbol) {
        return symbols.find(symbol);
    }

    /**Returns a set containing all constant symbols*/
    public Set<String> symbolSet() {
        return this.symbols.symbolSet();
    }
    public Set<String> spacedSymbolSet() {
        return this.symbols.spacedSymbolSet();
    }

    /**Adds an identifier to map if it doesn't exist*/
    public boolean addIdentifier(String key) {
        if (!this.idMap.containsKey(key)) {
            this.idMap.put(key, idMap.size());
            return true;
        }
        return false;
    }
    

    /**Finds and returns identifier from map*/
    public int getIdentifier(String key) {
        return this.idMap.get(key);
    }
    

    /**Returns a code for an identifier*/
    public int getIdentifierCode() {
        return this.symbols.getIdentifierCode();
    }

    /**Returns a code for a literal*/
    public int getLiteralCode() {
        return this.symbols.getLiteralCode();
    }
    
    public OperationResult analyzeSyntax(String[] string) throws PatternSearchException {
        SyntaxSymbol mainSymbol = syntax.getSyntaxSymbol(syntax.getMainSymbol());
        return mainSymbol.searchPatterns(string, 0);
    }
}
