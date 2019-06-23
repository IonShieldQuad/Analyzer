package generator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SymbolData {
    private String name;
    private Map<String, SymbolData> symbols = new HashMap<>();
    private Map<String, String> data = new HashMap<>();
    private Function<String, String> function;
    
    public Map<String, SymbolData> getSymbols() {
        return symbols;
    }
    
    public Map<String, String> getData() {
        return data;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public SymbolData(String name) {
        this.name = name;
    }
    
    public Function<String, String> getFunction() {
        return function;
    }
    
    public void setFunction(Function<String, String> function) {
        this.function = function;
    }
    
    public static SymbolData readString(String dataString) {
        return getSymbol(dataString, 0).symbolData;
    }
    
    /*@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : symbols.keySet()) {
            sb.append(key).append("$").append(symbols.get(key).toString()).append("|");
        }
        return "(" + name + ": " + sb.toString() + ")";
    }*/
    
    private static Result getSymbol(String dataString, int start) {
        int i = start;
        SymbolData s = null;
        Map<String, SymbolData> map = new HashMap<>();
        int paramStart = start;
        int paramDiv = start;
        int nameStart = start;
    
        while (i < dataString.length()) {
            
            
            if (dataString.charAt(i) == '(' && i > start) {
                Result r = getSymbol(dataString, i);
                s = r.symbolData;
                i = r.end + 1;
                continue;
            }
        
            if (dataString.charAt(i) == '|') {
                if (i > paramStart + 1) {
                    SymbolData extra = new SymbolData(dataString.substring(paramStart + 1, paramDiv));
    
                    map.put(dataString.substring(paramDiv + 2, i), s != null ? s : extra);
                }
                
                paramStart = i;
                paramDiv = i;
                s = null;
                i++;
                continue;
            }
        
            if (dataString.charAt(i) == '@' && dataString.charAt((i + 1)) == '$') {
                if (s == null) {
                    s = new SymbolData(dataString.substring(paramStart + 1, i));
                }
                paramDiv = i;
                i++;
                continue;
            }
        
            if (dataString.charAt(i) == '#') {
                if (i > paramStart + 1) {
                    SymbolData extra = new SymbolData(dataString.substring(paramStart + 1, paramDiv));
    
                    map.put(dataString.substring(paramDiv + 2, i), s != null ? s : extra);
                }
    
                paramStart = i;
                paramDiv = i;
                nameStart = i;
                s = null;
                i++;
                continue;
            }
        
            if (dataString.charAt(i) == ')') {
                SymbolData resData = new SymbolData(dataString.substring(nameStart + 1, i));
                resData.symbols.putAll(map);
                return new Result(resData, start, i);
            }
            
            i++;
        }
        return new Result(null, start, i);
    }
    
    
    private static class Result {
        SymbolData symbolData;
        int start;
        int end;
    
        Result(SymbolData symbolData, int start, int end) {
            this.symbolData = symbolData;
            this.start = start;
            this.end = end;
        }
    }
}
