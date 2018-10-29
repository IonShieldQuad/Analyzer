package syntax;


import core.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.security.InvalidKeyException;
import java.util.*;

public abstract class PrecedenceTable {
    private Map<String, Map<String, Precedence>> map;
    private List<String> keys;
    
    public enum Precedence {
        NONE, HIGHER, EQUAL, LOWER
    }
    
    public PrecedenceTable() {
        keys = new ArrayList<>();
        map = new HashMap<>();
        
        init();
    }
    
    protected abstract void init();
    
    public Precedence get(String x, String y) {
        return map.get(x).get(y);
    }
    
    public void set(String x, String y, Precedence value) throws InvalidKeyException {
        if (!contains(x) || !contains(y)) {
            if (!contains(x)) {
                throw new InvalidKeyException("Key \"" + x + "\" not found");
            }
            if (!contains(y)) {
                throw new InvalidKeyException("Key \"" + y + "\" not found");
            }
        }
        map.get(x).put(y, value);
    }
    
    protected void add(String key) {
        if (contains(key)) {
            throw new KeyAlreadyExistsException("Key \"" + key + "\" already exists");
        }
        
        map.put(key, new HashMap<>());
        keys.forEach((k) -> map.get(key).put(k, Precedence.NONE));
        keys.add(key);
        map.forEach((k, v) -> v.put(key, Precedence.NONE));
    }
    
    public boolean contains(String key) {
        return keys.contains(key);
    }
    
    public int size() {
        return keys.size();
    }
    
    public boolean isEmpty() {
        return size() == 0;
    }
    
    public List<String> getKeys() {
        return new ArrayList<>(keys);
    }
    
    @Contract(pure = true)
    public static Precedence calculatePrecedence(int a, int b) {
        if (a > b) {
            return Precedence.HIGHER;
        }
        if (a < b) {
            return Precedence.LOWER;
        }
        return Precedence.EQUAL;
    }
    
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static PrecedenceTable fromPack(@NotNull SyntaxPack pack) throws PatternSearchException {
        List<PrecedenceTuple> tuples = new ArrayList<>();
        
        List<SyntaxSymbol> startedSymbols = new ArrayList<>();
        List<SyntaxSymbol> finishedSymbols = new ArrayList<>();
        Map<SyntaxSymbol, SymbolData> dataMap = new HashMap<>();
        
        pack.getSyntaxSymbol(pack.getMainSymbol()).analyze(startedSymbols, finishedSymbols, dataMap, tuples);
        
        startedSymbols.forEach(s -> System.out.println(s.getName()));
    
        Logger.getInstance().logln("tableGen", "\nResult:");
        tuples.sort(Comparator.comparing((e) -> e.x));
        for (PrecedenceTuple t : tuples) {
            System.out.println(t.x + " " + t.value + " " + t.y);
            Logger.getInstance().logln("tableGen", t.x + " " + t.value + " " + t.y);
        }
        
        return new PrecedenceTable(){
            @Override
            protected void init() {
                
                for (SyntaxSymbol s : pack.symbolSet()) {
                    if (s.getTerm() != null) {
                        this.add(s.getTerm());
                    }
                }
                this.add(Integer.toString(pack.getIdentifierCode()));
                this.add(Integer.toString(pack.getLiteralCode()));
                
                for (PrecedenceTuple t : tuples) {
                    try {
                        this.set(t.x, t.y, t.value);
                    } catch (InvalidKeyException ignored) {}
                }
                
            }
        };
    }
    
    static class PrecedenceTuple {
        final String x;
        final String y;
        final Precedence value;
        
        PrecedenceTuple(String x, String y, Precedence value) {
    
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
    
    static class OperatorSection {
        private SyntaxSymbol startTerminal;
        private SyntaxSymbol endTerminal;
        private List<SyntaxSymbol> nonTerminalList;
        
        OperatorSection(SyntaxSymbol startTerminal, SyntaxSymbol endTerminal, List<SyntaxSymbol> nonTerminalList) {
    
            this.startTerminal = startTerminal;
            this.endTerminal = endTerminal;
            this.nonTerminalList = nonTerminalList;
        }
    
        public List<SyntaxSymbol> getNonTerminalList() {
            return nonTerminalList;
        }
    
        public SyntaxSymbol getStartTerminal() {
            return startTerminal;
        }
    
        public SyntaxSymbol getEndTerminal() {
            return endTerminal;
        }
    }
    
    static class SymbolData {
        private SyntaxSymbol symbol;
    
        public List<List<OperatorSection>> getData() {
            return data;
        }
    
        private List<List<OperatorSection>> data = new ArrayList<>();
        private List<SyntaxSymbol> extraStartTerms = new ArrayList<>();
        private List<SyntaxSymbol> extraEndTerms = new ArrayList<>();
    
        SymbolData(SyntaxSymbol symbol) {
            this.symbol = symbol;
        }
    
        public OperatorSection get (int seq, int index) {
            return data.get(seq).get(index);
        }
        
        public void add(int seq, List<OperatorSection> list) {
            data.add(seq, list);
        }
        
        List<SyntaxSymbol> getStartTerms(List<SyntaxSymbol> startedSymbols, List<SyntaxSymbol> finishedSymbols, Map<SyntaxSymbol, SymbolData> dataMap, List<PrecedenceTuple> tuples) throws PatternSearchException {
            List<SyntaxSymbol> storage = new ArrayList<>();
            
            for (List<OperatorSection> list : data) {
                OperatorSection el = list.get(0);
                if (el.startTerminal == null) {
                    for (SyntaxSymbol nonterm : el.getNonTerminalList()) {
                        
                        if (startedSymbols.contains(nonterm)) {
                            storage.addAll(dataMap.get(nonterm).getStartTerms(startedSymbols, finishedSymbols, dataMap, tuples));
                        }
                        else {
                            nonterm.analyze(startedSymbols, finishedSymbols, dataMap, tuples);
                            storage.addAll(dataMap.get(nonterm).getStartTerms(startedSymbols, finishedSymbols, dataMap, tuples));
                        }
                        
                    }
                }
                else {
                    storage.add(el.startTerminal);
                }
            }
            
            storage.addAll(extraStartTerms);
            
            return storage;
        }
        
        List<SyntaxSymbol> getEndTerms(List<SyntaxSymbol> startedSymbols, List<SyntaxSymbol> finishedSymbols, Map<SyntaxSymbol, SymbolData> dataMap, List<PrecedenceTuple> tuples) throws PatternSearchException {
            List<SyntaxSymbol> storage = new ArrayList<>();
    
            for (List<OperatorSection> list : data) {
                OperatorSection el = list.get(0);
                if (el.endTerminal == null) {
                    for (SyntaxSymbol nonterm : el.getNonTerminalList()) {
                
                        if (startedSymbols.contains(nonterm)) {
                            storage.addAll(dataMap.get(nonterm).getEndTerms(startedSymbols, finishedSymbols, dataMap, tuples));
                        }
                        else {
                            nonterm.analyze(startedSymbols, finishedSymbols, dataMap, tuples);
                            storage.addAll(dataMap.get(nonterm).getEndTerms(startedSymbols, finishedSymbols, dataMap, tuples));
                        }
                
                    }
                }
                else {
                    storage.add(el.startTerminal);
                }
            }
    
            storage.addAll(extraStartTerms);
    
            return storage;
        }
    
        public List<SyntaxSymbol> getExtraStartTerms() {
            return extraStartTerms;
        }
    
        public List<SyntaxSymbol> getExtraEndTerms() {
            return extraEndTerms;
        }
    }
}
