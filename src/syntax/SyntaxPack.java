package syntax;

import lexis.SymbolPack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Abstract class containing syntax rules
 */
public abstract class SyntaxPack {
    
    public static final String ID_NAME = "analyzer identifier";
    public static final String LIT_NAME = "analyzer literal";
    
    private Map<String, SyntaxSymbol> syntax = new HashMap<>();
    private String mainSymbol;
    private int identifierCode;
    private int literalCode;
    
    protected SyntaxPack() {
        init();
        addSyntaxSymbol(ID_NAME, null, Integer.toString(getIdentifierCode()));
        addSyntaxSymbol(LIT_NAME, null, Integer.toString(getLiteralCode()));
    }
    
    protected abstract void init();
    
    public SyntaxSymbol getSyntaxSymbol(String name) {
        if (!syntax.containsKey(name)) {
            throw new IllegalArgumentException("Symbol \"" + name + "\" not found");
        }
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
    
    public Map<String, String> termToNameMap() {
        Map<String, String> map = new HashMap<>();
        
        symbolSet().forEach(s -> {
            if (s.getTerm() != null) {
                switch (s.getName()) {
                    case ID_NAME:
                        map.put(s.getTerm(), "id");
                        break;
                    case LIT_NAME:
                        map.put(s.getTerm(), "lit");
                        break;
                    default:
                        map.put(s.getTerm(), s.getName());
                        break;
                }
            }
        });
        return map;
    }
    
}
