package syntax;

import core.SymbolsSystem;
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
    
    private SymbolsSystem system;
    private SymbolPack lexis;
    private Map<String, SyntaxSymbol> syntax = new HashMap<>();
    private String mainSymbol;
    private int identifierCode;
    private int literalCode;
    
    protected SyntaxPack() {
        init();
        if (lexis == null) {
            setSymbolPack(new SymbolPack() {
                @Override
                protected void initSymbols() {
        
                }
            });
        }
        addTerminalsFromPack(lexis);
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
    
    public SyntaxSymbol addSyntaxSymbol(String name, SyntaxOperation[][] patterns, String term) {
        return new SyntaxSymbol(this, name, patterns, term);
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
    
    protected void setSymbolPack(SymbolPack pack) {
        lexis = pack;
    }
    
    protected SymbolPack getSymbolPack() {
        return lexis;
    }
    
    
    
    private void addTerminalsFromPack(SymbolPack pack) {
        for (String s : pack.symbolSet()) {
            addSyntaxSymbol(s, null, Integer.toString(pack.find(s)));
        }
        setIdentifierCode(pack.getIdentifierCode());
        setLiteralCode(pack.getLiteralCode());
    }
    
    public int extractIdentifier(String input) {
        return lexis.extractIdentifier(input);
    }
    
    public int extractLiteral(String input) {
        return lexis.extractLiteral(input);
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
    
    public SymbolsSystem getSystem() {
        return system;
    }
    
    public void setSystem(SymbolsSystem system) {
        this.system = system;
    }
    
    public void setTypeOfId(int id, String type) {
        system.setTypeOfId(id, type);
    }
    
    public String getTypeOfId(int id) {
        return system.getTypeOfId(id);
    }
}
