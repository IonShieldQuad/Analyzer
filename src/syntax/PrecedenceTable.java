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
        Map<SyntaxSymbol, SymbolData> dataMap = new HashMap<>();
        
        analyze(pack.getSyntaxSymbol(pack.getMainSymbol()), dataMap, tuples);
    
        System.out.println("Analyzed symbols:");
        Logger.getInstance().logln("tableGen", "Analyzed symbols:");
        dataMap.keySet().forEach(s -> System.out.println(s.getName()));
    
        Logger.getInstance().logln("tableGen", "\nResult:");
        tuples.sort((a, b) -> {
            if (a.x.equals(b.x)) {
                return a.y.compareTo(b.y);
            }
            else {
                return  a.x.compareTo(b.x);
            }
        });
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
                //this.add(Integer.toString(pack.getIdentifierCode()));
                //this.add(Integer.toString(pack.getLiteralCode()));
                
                for (PrecedenceTuple t : tuples) {
                    try {
                        this.set(t.x, t.y, t.value);
                    } catch (InvalidKeyException ignored) {}
                }
                
            }
        };
    }
    
    static void analyze(SyntaxSymbol symbol, Map<SyntaxSymbol, SymbolData> dataMap, List<PrecedenceTuple> tuples) throws PatternSearchException {
    
        Logger.getInstance().logln("tableGen", "Analyzing: " + symbol.getName());
        
        if (Arrays.stream(symbol.getPatterns()).anyMatch(p -> Arrays.stream(p).anyMatch(op -> op.isSelectionStart() || op.isSelectionEnd() || op.isSelectionBody() || op.isLoopStart() || op.isLoopEnd()))) {
            throw new PatternSearchException(symbol.getName(), null, 0, "Loop and select operation not supported");
        }
        
        SymbolData data = new SymbolData(symbol);
        dataMap.put(symbol, data);
        data.setAnalyzed(true);
        
        for (int i = 0; i < symbol.getPatterns().length; i++) {
            
            SyntaxSymbol prev = null;
            int prevIndex = -1;
            List<SymbolSegment> list = new ArrayList<>();
            
            for (int j = 0; j < symbol.getPatterns()[i].length; j++) {
                SyntaxOperation op = symbol.getPatterns()[i][j];
                
                if (op.isSymbol() && (op.isLiteral() || op.isIdentifier() || symbol.getPack().getSyntaxSymbol(op.getData()).getTerm() != null)) {
                    
                    SyntaxSymbol s;
                    
                    if (op.isIdentifier()) {
                        s = symbol.getPack().getSyntaxSymbol("analyzer identifier");
                    }
                    else if (op.isLiteral()) {
                        s = symbol.getPack().getSyntaxSymbol("analyzer literal");
                    }
                    else {
                        s = symbol.getPack().getSyntaxSymbol(op.getData());
                    }
                    
                    
                    List<SyntaxSymbol> nonterms = new ArrayList<>();
                    for (int k = prevIndex + 1; k < j; k++) {
                        if (symbol.getPatterns()[i][k].isSymbol()) {
                            nonterms.add(symbol.getPack().getSyntaxSymbol(symbol.getPatterns()[i][k].getData()));
                        }
                    }
                    
                    list.add(new SymbolSegment(prev, s, nonterms));
                    
                    prev = s;
                    prevIndex = j;
                }
                
            }
            
            if (prevIndex < symbol.getPatterns()[i].length - 1 || symbol.getPatterns()[i].length == 0) {
                
                List<SyntaxSymbol> nonterms = new ArrayList<>();
                for (int k = prevIndex + 1; k < symbol.getPatterns()[i].length; k++) {
                    if (symbol.getPatterns()[i][k].isSymbol()) {
                        nonterms.add(symbol.getPack().getSyntaxSymbol(symbol.getPatterns()[i][k].getData()));
                    }
                }
                
                list.add(new SymbolSegment(prev, null, nonterms));
            }
            
            data.add(i, list);
            
            list.forEach(el -> Logger.getInstance().logln("tableGen", (el.getStartTerminal() == null ? "null" : el.getStartTerminal().getName()) + "" + el.getNonTerminalList().stream().collect(StringBuilder::new, (b, s) -> b.append(" | ").append(s.getName()), StringBuilder::append) + " | " + (el.getEndTerminal() == null ? "null" : el.getEndTerminal().getName())));
            
        }
        
        
        for (List<SymbolSegment> list : data.getData()) {
            for (SymbolSegment section : list) {
                
                if (section.getStartTerminal() != null && section.getEndTerminal() != null) {
                    tuples.add(new PrecedenceTuple(section.getStartTerminal().getTerm(), section.getEndTerminal().getTerm(), Precedence.EQUAL));
                }
                
                if (section.getStartTerminal() != null) {
                    for (SyntaxSymbol nonterm : section.getNonTerminalList()) {
    
                        if (!dataMap.keySet().contains(nonterm)) {
                            dataMap.put(nonterm, new SymbolData(nonterm));
                        }
                        
                        for (SyntaxSymbol startTerm : dataMap.get(nonterm).getStartTerms(dataMap, tuples)) {
    
                            String term;
                            switch (startTerm.getName()) {
                                case "analyzer identifier":
                                    term = Integer.toString(startTerm.getPack().getIdentifierCode());
                                    break;
                                case "analyzer literal":
                                    term = Integer.toString(startTerm.getPack().getLiteralCode());
                                    break;
                                default:
                                    term = startTerm.getTerm();
                                    break;
                            }
                            
                            if (nonterm.isInlinePrecedence()) {
                                tuples.add(new PrecedenceTuple(section.getStartTerminal().getTerm(), term, Precedence.EQUAL));
                            } else {
                                tuples.add(new PrecedenceTuple(section.getStartTerminal().getTerm(), term, Precedence.LOWER));
                            }
                        }
                        
                    }
                }
                
                if (section.getEndTerminal() != null) {
                    for (SyntaxSymbol nonterm : section.getNonTerminalList()) {
    
                        if (!dataMap.keySet().contains(nonterm)) {
                            dataMap.put(nonterm, new SymbolData(nonterm));
                        }
                        
                        for (SyntaxSymbol endTerm : dataMap.get(nonterm).getEndTerms(dataMap, tuples)) {
                            
                            String term;
                            switch (endTerm.getName()) {
                                case "analyzer identifier":
                                    term = Integer.toString(endTerm.getPack().getIdentifierCode());
                                    break;
                                case "analyzer literal":
                                    term = Integer.toString(endTerm.getPack().getLiteralCode());
                                    break;
                                default:
                                    term = endTerm.getTerm();
                                    break;
                            }
                            
                            if (nonterm.isInlinePrecedence()) {
                                tuples.add(new PrecedenceTuple(term, section.getEndTerminal().getTerm(), Precedence.EQUAL));
                            } else {
                                tuples.add(new PrecedenceTuple(term, section.getEndTerminal().getTerm(), Precedence.HIGHER));
                            }
                        }
                        
                    }
                }
                
            }
        }
        
        for (List<SymbolSegment> list : data.getData()) {
            if (!list.isEmpty()) {
                SymbolSegment section = list.get(0);
                
                if (section.getStartTerminal() == null) {
                    
                    for (SyntaxSymbol nonterm : section.getNonTerminalList()) {
    
                        if (!dataMap.keySet().contains(nonterm)) {
                            dataMap.put(nonterm, new SymbolData(nonterm));
                        }
                        
                        data.getExtraStartTerms().addAll(dataMap.get(nonterm).getStartTerms(dataMap, tuples));
                    }
                }
                
                section = list.get(list.size() - 1);
                
                if (section.getEndTerminal() == null) {
                    
                    for (SyntaxSymbol nonterm : section.getNonTerminalList()) {
    
                        if (!dataMap.keySet().contains(nonterm)) {
                            dataMap.put(nonterm, new SymbolData(nonterm));
                        }
                        
                        data.getExtraEndTerms().addAll(dataMap.get(nonterm).getEndTerms(dataMap, tuples));
                    }
                }
            }
        }
        
        data.setFinished(true);
        
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
    
    static class SymbolSegment {
        private SyntaxSymbol startTerminal;
        private SyntaxSymbol endTerminal;
        private List<SyntaxSymbol> nonTerminalList;
        
        SymbolSegment(SyntaxSymbol startTerminal, SyntaxSymbol endTerminal, List<SyntaxSymbol> nonTerminalList) {
    
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
    
        public List<List<SymbolSegment>> getData() {
            return data;
        }
    
        private List<List<SymbolSegment>> data = new ArrayList<>();
        private List<SyntaxSymbol> extraStartTerms = new ArrayList<>();
        private List<SyntaxSymbol> extraEndTerms = new ArrayList<>();
        
        private boolean analyzed = false;
        private boolean finished = false;
        private int counterStart = 0;
        private int counterEnd = 0;
    
        SymbolData(SyntaxSymbol symbol) {
            this.symbol = symbol;
        }
    
        public SymbolSegment get (int seq, int index) {
            return data.get(seq).get(index);
        }
        
        public void add(int seq, List<SymbolSegment> list) {
            data.add(seq, list);
        }
        
        List<SyntaxSymbol> getStartTerms(Map<SyntaxSymbol, SymbolData> dataMap, List<PrecedenceTuple> tuples) throws PatternSearchException {
            
            if (!isAnalyzed()) {
                analyze(symbol, dataMap, tuples);
                return dataMap.get(symbol).getStartTerms(dataMap, tuples);
            }
            
            counterStart++;
            
            List<SyntaxSymbol> storage = new ArrayList<>();
            
            for (List<SymbolSegment> list : data) {
                SymbolSegment el = list.get(0);
                if (el.startTerminal == null) {
    
                    if (el.endTerminal != null) {
                        storage.add(el.endTerminal);
                    }
                    
                    for (SyntaxSymbol nonterm : el.getNonTerminalList()) {
                        
                        if (dataMap.keySet().contains(nonterm) && dataMap.get(nonterm).isAnalyzed()) {
                            if (counterStart <= 1) {
                                storage.addAll(dataMap.get(nonterm).getStartTerms(dataMap, tuples));
                            }
                        }
                        else {
                            analyze(nonterm, dataMap, tuples);
                            storage.addAll(dataMap.get(nonterm).getStartTerms(dataMap, tuples));
                        }
                        
                    }
                }
                else {
                    storage.add(el.startTerminal);
                }
            }
            
            counterStart--;
            storage.addAll(extraStartTerms);
            
            return storage;
        }
        
        List<SyntaxSymbol> getEndTerms(Map<SyntaxSymbol, SymbolData> dataMap, List<PrecedenceTuple> tuples) throws PatternSearchException {
    
            if (!isAnalyzed()) {
                analyze(symbol, dataMap, tuples);
                return dataMap.get(symbol).getEndTerms(dataMap, tuples);
            }
            
            counterEnd++;
            
            List<SyntaxSymbol> storage = new ArrayList<>();
    
            for (List<SymbolSegment> list : data) {
                SymbolSegment el = list.get(list.size() - 1);
                if (el.endTerminal == null) {
    
                    if (el.startTerminal != null) {
                        storage.add(el.startTerminal);
                    }
                    
                    for (SyntaxSymbol nonterm : el.getNonTerminalList()) {
                
                        if (dataMap.keySet().contains(nonterm) && dataMap.get(nonterm).isAnalyzed()) {
                            if (counterEnd <= 1) {
                                storage.addAll(dataMap.get(nonterm).getEndTerms(dataMap, tuples));
                            }
                        }
                        else {
                            analyze(nonterm, dataMap, tuples);
                            storage.addAll(dataMap.get(nonterm).getEndTerms(dataMap, tuples));
                        }
                
                    }
                }
                else {
                    storage.add(el.endTerminal);
                }
            }
            
            counterEnd--;
            storage.addAll(extraEndTerms);
    
            return storage;
        }
    
        public List<SyntaxSymbol> getExtraStartTerms() {
            return extraStartTerms;
        }
    
        public List<SyntaxSymbol> getExtraEndTerms() {
            return extraEndTerms;
        }
    
        public boolean isAnalyzed() {
            return analyzed;
        }
    
        public void setAnalyzed(boolean analyzed) {
            this.analyzed = analyzed;
        }
    
        public boolean isFinished() {
            return finished;
        }
    
        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }
}
