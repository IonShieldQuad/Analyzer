package syntax;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public void set(String x, String y, Precedence value) {
        if (!contains(x) || !contains(y)) {
            throw new IllegalStateException("Key not found");
        }
        map.get(x).put(y, value);
    }
    
    protected void add(String key) {
        if (contains(key)) {
            throw new IllegalStateException("Key \"" + key + "\" already exists");
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
    
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static PrecedenceTable fromPack(SyntaxPack pack) {
        List<PrecedenceTuple> tuples = new ArrayList<>();
        //TODO: implement table generation
        
        return new PrecedenceTable(){
            @Override
            protected void init() {
                
                for (SyntaxSymbol s : pack.symbolSet()) {
                    if (s.getTerm() != null) {
                        this.add(s.getName());
                    }
                }
                
                for (PrecedenceTuple t : tuples) {
                    this.set(t.x, t.y, t.value);
                }
                
            }
        };
    }
    
    private static class PrecedenceTuple {
        final String x;
        final String y;
        final Precedence value;
        
        PrecedenceTuple(String x, String y, Precedence value) {
    
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
}
