package syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationResult {
    private final int oldPosition;
    private final int newPosition;
    private final boolean success;
    private final String data;
    private final SyntaxError error;
    
    OperationResult(int oldPosition, int newPosition, boolean success, String data, @Nullable SyntaxError error) {
        
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = data;
        }
        else {
            this.data = "";
        }
 
        this.error = error;
    }
    
    
    @Override
    public String toString() {
        return data;
        /*StringBuilder out = new StringBuilder();
        for (DataEntry s : getData()) {
            if (s == null || s.toString().length() == 0) {
                continue;
            }
            out.append(s);
        }
        return out.toString();*/
    }
    
    /*public String toString(String separator) {
        StringBuilder out = new StringBuilder();
        String prev = separator;
        for (DataEntry s : data) {
            if (s == null || s.toString().length() == 0) {
                continue;
            }
        
            if (!(s.toString().startsWith(separator) || prev.endsWith(separator))) {
                out.append(separator);
            }
        
        out.append(s);
        prev = s.toString();
        }
    return out.toString();
    }*/
    
    public SyntaxError getError() {
        return error;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    int getNewPosition() {
        return newPosition;
    }
    
    int getOldPosition() {
        return oldPosition;
    }
    
    
    public String getData() {
        return data;
    }
    
    public static class SyntaxError {
        private final String message;
        private final String symbol;
        private final int index;
        
        SyntaxError(String message, String symbol, int index) {
        
            this.message = message;
            this.symbol = symbol;
            this.index = index;
        }
        
        @Override
        public String toString() {
            return message;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public int getIndex() {
            return index;
        }
    }
    
    public static class DataEntry {
        private final String string;
        private final Map<String, List<String>> variables;
    
    
        /**
         * Creates a new data entry with just a string
         * @param string string to store
         */
        DataEntry(String string) {
    
            variables = null;
            this.string = string;
        }
    
        /**
         * Creates a new data entry with string and variable list
         * @param string string to store
         * @param variables list of variables
         */
        DataEntry(String string, Map<String, List<String>> variables) {
        
            this.variables = new HashMap<>(variables);
            this.string = string;
        }
        
    
        public boolean hasVariables() {
            return (variables != null && !variables.isEmpty() && variables.values().stream().noneMatch(List::isEmpty));
        }
        
        public List<String> get(String key) {
            if (variables == null) {
                return null;
            }
            if (!hasVariables()) {
                return new ArrayList<>();
            }
            return variables.get(key);
        }
        
        @Override
        public String toString() {
            return string;
        }
        
        public static List<DataEntry> fromStringList(@NotNull List<String> strings) {
            return strings.stream().map(DataEntry::new).collect(ArrayList::new, List::add, List::addAll);
        }
    
        public Map<String, List<String>> getVariables() {
            return variables;
        }
    }
}

