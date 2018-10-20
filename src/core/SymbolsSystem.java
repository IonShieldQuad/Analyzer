package core;

import lexis.StringLexer;
import lexis.SymbolPack;
import lexis.UnmatchedSubstringException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SymbolsSystem implements Lexer {

    private StringLexer lexer = new StringLexer(this);
    protected SymbolPack symbols;
    protected Map<String, Integer> idMap = new HashMap<>();

    public SymbolsSystem(SymbolPack symbolPack) {
        this.symbols = symbolPack;
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
    
    @Override
    public String[] process(String input) throws UnmatchedSubstringException {
        return lexer.processString(input);
    }
}
