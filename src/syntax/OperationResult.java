package syntax;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OperationResult {
    private final int oldPosition;
    private final int newPosition;
    private final boolean success;
    private final String data[];
    private final SyntaxError error;
    
    OperationResult(int oldPosition, int newPosition, boolean success, List<String> data, @Nullable SyntaxError error) {
    
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = data.toArray(new String[0]);
        }
        else {
            this.data = new String[0];
        }
        this.error = error;
    }
    
    OperationResult(int oldPosition, int newPosition, boolean success, String data, @Nullable SyntaxError error) {
        
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.success = success;
        if (data != null) {
            this.data = new String[]{data};
        }
        else {
            this.data = new String[0];
        }
        this.error = error;
    }
    
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (String s : getData()) {
            if (s == null || s.trim().length() == 0) {
                continue;
            }
            out.append(s);
        }
        return out.toString();
    }
    
    public String toString(String separator) {
        StringBuilder out = new StringBuilder();
        String prev = separator;
        for (String s : data) {
            if (s == null || s.length() == 0) {
                continue;
            }
        
            if (!(s.startsWith(separator) || prev.endsWith(separator))) {
                out.append(separator);
            }
        
        out.append(s);
        prev = s;
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
    
    public List<String> getData() {
        return new ArrayList<>(Arrays.asList(data));
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
}

