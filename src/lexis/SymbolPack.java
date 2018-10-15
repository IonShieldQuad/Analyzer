package lexis;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SymbolPack {

    public SymbolPack() {
        initSymbols();
    }

    private final Map<String, Integer> symbolsMap = new HashMap<>();
    private final Map<String, Integer> spacedSymbolsMap = new HashMap<>();
    private int identifierCode;
    private int literalCode;

    protected void add(String symbol) {
        this.symbolsMap.put(symbol, this.getSymbolCount());
    }
    protected void add(String symbol, int index) {
        this.symbolsMap.put(symbol, index);
    }
    protected void addSpaced(String symbol) {
        int index = this.getSymbolCount();
        this.spacedSymbolsMap.put(symbol, index);
        this.add(symbol, index);
    }
    protected void addSpaced(String symbol, int index) {
        this.spacedSymbolsMap.put(symbol, index);
        this.add(symbol, index);
    }

    /**Called after construction to add symbols to the map*/
    protected abstract void initSymbols();

    protected final void setIdentifierCode(int in) {
        this.identifierCode = in;
    }
    protected final void setLiteralCode(int in) {
        this.literalCode = in;
    }

    int getSymbolCount() {
        return this.symbolsMap.size();
    }

    @Contract(pure = true)
    public final int getIdentifierCode() {
        return this.identifierCode;
    }
    @Contract(pure = true)
    public final int getLiteralCode() {
        return this.literalCode;
    }

    public final int find(String symbol) {
        return this.symbolsMap.get(symbol);
    }
    
    @Nullable
    public final String find(int symbol) {
        for (var pair : symbolsMap.entrySet()) {
            if (pair.getValue() == symbol) {
                return pair.getKey();
            }
        }
        return null;
    }

    @NotNull
    @Contract(pure = true)
    public final Set<String> symbolSet() {
        return this.symbolsMap.keySet();
    }
    @NotNull
    @Contract(pure = true)
    public final Set<String> spacedSymbolSet() {
        return this.spacedSymbolsMap.keySet();
    }
}