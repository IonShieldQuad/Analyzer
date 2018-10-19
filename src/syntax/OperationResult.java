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
    private final List<DataEntry> data;
    private final SyntaxError error;
    private final Map<String, List<String>> variables;
    
    OperationResult(int oldPosition, int newPosition, boolean success, List<DataEntry> data, @Nullable SyntaxError error) {
        
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = new ArrayList<>(data);
        }
        else {
            this.data = new ArrayList<>();
        }
        this.variables = new HashMap<>();
        this.data.stream()
                .filter(DataEntry::hasVariables)
                .map(DataEntry::getVariables)
                .forEach((m) -> m.keySet().forEach((k) -> {
                    if (this.variables.containsKey(k)) {
                        this.variables.get(k).addAll(m.get(k));
                    }
                    else {
                        this.variables.put(k, new ArrayList<>(m.get(k)));
                    }
                }));
        this.error = error;
    }
    
    OperationResult(int oldPosition, int newPosition, boolean success, String data, @Nullable SyntaxError error) {
        
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = new ArrayList<>();
            this.data.add(new DataEntry(data));
        }
        else {
            this.data = new ArrayList<>();
        }
        variables = new HashMap<>();
        this.error = error;
    }
    
    OperationResult(int oldPosition, int newPosition, boolean success, String data, Map<String, List<String>> variables, @Nullable SyntaxError error) {
        
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = new ArrayList<>();
            this.data.add(new DataEntry(data, variables));
        }
        else {
            this.data = new ArrayList<>();
        }
        this.variables = new HashMap<>();
        this.data.stream()
                .filter(DataEntry::hasVariables)
                .map(DataEntry::getVariables)
                .forEach((m) -> m.keySet().forEach((k) -> {
                    if (this.variables.containsKey(k)) {
                        this.variables.get(k).addAll(m.get(k));
                    }
                    else {
                        this.variables.put(k, new ArrayList<>(m.get(k)));
                    }
                }));
        this.error = error;
    }
    
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (DataEntry s : getData()) {
            if (s == null || s.toString().length() == 0) {
                continue;
            }
            out.append(s);
        }
        return out.toString();
    }
    
    public String toString(String separator) {
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
    }
    
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
    
    List<String> getVariableValues(String var) {
        return variables.get(var);
    }
    
    Map<String, List<String>> getVariables() {
        return variables;
    }
    
    public List<DataEntry> getData() {
        return new ArrayList<>(data);
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

