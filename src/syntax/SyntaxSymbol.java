package syntax;

import core.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Class containing data about a symbol*/
public class SyntaxSymbol {
    private final SyntaxPack pack;
    private final String name;
    private final SyntaxOperation[][] patterns;
    private final String term;
    private boolean inlinePrecedence = false;
    
    /**
     * @param pack Syntax pack to which new symbol will be added
     * @param name Name of the new symbol
     * @param patterns Patterns of syntax operations to search
     * @param term If this is a terminal symbol, it's value. Null otherwise
     */
    SyntaxSymbol(@NotNull SyntaxPack pack, @NotNull String name, @Nullable SyntaxOperation[][] patterns, @Nullable String term) {
        this.pack = pack;
        this.name = name;
        if (patterns != null) {
            this.patterns = new SyntaxOperation[patterns.length][];
            for (int i = 0; i < this.patterns.length; ++i) {
                this.patterns[i] = Arrays.copyOf(patterns[i], patterns[i].length);
            }
        }
        else {
            this.patterns = null;
        }
        this.term = term;
        this.pack.addSyntaxSymbol(name, this);
    }
    
    String getName() {
        return name;
    }
    
    SyntaxOperation[][] getPatterns() {
        return patterns;
    }
    
    SyntaxPack getPack() {
        return pack;
    }
    
    /**
     * Perform search of patterns in data starting at index
     * @param data Input data sa an array of strings
     * @param index Index to begin search at
     * @return Object containing result information
     * */
    @NotNull
    public OperationResult searchPatterns(@NotNull String[] data, int index) throws PatternSearchException {
        List<String> out = new ArrayList<>();
        //Map containing variables
        Map<String, String> vars = new HashMap<>();
        //Map containing data about identifier type assignment
        Map<String, String> types = new HashMap<>();
        OperationResult.SyntaxError error = null;
        
        for (SyntaxOperation[] pattern : patterns) {
            int position = index;
            boolean success = true;
            OperationResult.SyntaxError err = new OperationResult.SyntaxError("Unknown error occurred", null, 0);

            out.clear();
            Stack<LoopData> loops = new Stack<>();
            Stack<SelectData> selects = new Stack<>();
            vars.clear();
            types.clear();

            for (int i = 0; i < pattern.length;) {
                SyntaxOperation op = pattern[i];

                //Checks for loop start
                if (op.isLoopStart() && (loops.isEmpty() || loops.peek().getStart() != i)) {
                    loops.push(findLoop(position, pattern, i));
                }

                //Checks for loop end
                if (op.isSelectionStart() && (selects.isEmpty() || selects.peek().getStart() != i)) {
                    selects.push(findSelect(position, pattern, i));
                }
                
                Logger.getInstance().logln("syntax", name + ": " + i + " enter");
                
                //Tries to perform operation
                OperationResult res = performOperation(op, data, position);
                
                Logger.getInstance().logln("syntax", "(" + data[position] + ") " + res.getOldPosition() + " -> " + (res.isSuccess() ? res.getNewPosition() : "\"" + res.getError() + "\"") + (loops.isEmpty() ? "" : " l") + (selects.isEmpty() ? "" : " s") + " : " + name + ": " + i + " exit");
    
                //Stores variables
                if (op.containsVariables()) {
                    op.getVariables().forEach(v -> vars.put(v, res.toString()));
                }
                
                //Stores identifier type
                if (res.isSuccess() && op.isIdType()) {
                    for (String name : op.idsTypeList()) {
                        types.put(name, res.toString());
                    }
                }
                
                //Saves error with highest index
                if (res.getError() != null && (err == null || res.getError().getIndex() > err.getIndex())) {
                    err = res.getError();
                }
                if (err != null && (error == null || err.getIndex() > error.getIndex())) {
                    error = err;
                }
                
                //Breaks if operation failed and not in loop or select
                if (!res.isSuccess() && loops.isEmpty() && selects.isEmpty()) {
                    success = false;
                    break;
                }

                if (res.isSuccess()) {
                    //Updates position and data if operation succeeded
                    position = res.getNewPosition();
                    out.add(res.getData());
                    
                    if (!selects.isEmpty()) {
                        if (selects.peek().hasPoint(i + 1) && selects.peek().getStart() != i) {
                            i = selects.pop().getEnd();
                        }
                    }
                }
                else {
                    boolean loop;
                    
                    //Checks whether loop or select was started last
                    if (loops.isEmpty()) {
                        loop = false;
                    }
                    else if (selects.isEmpty()) {
                        loop = true;
                    }
                    else {
                        loop = selects.peek().getStart() <= loops.peek().getStart();
                    }
                    
                    if (loop) {
                        //If the loop was last break and return to the beginning
                        position = loops.peek().getStartIndex();
                        i = loops.pop().getEnd();
                    }
                    else if (!selects.isEmpty()) {
                        //If in selection tries to try next option
                        position = selects.peek().getStartIndex();
                        if (selects.peek().getEnd() == selects.peek().getNext(i)) {
                            
                            selects.pop();
                            
                            //Breaks if select is ended with failure
                            if (loops.isEmpty() && selects.isEmpty()) {
                                success = false;
                                break;
                            }
                            else if (!loops.isEmpty()) {
                                position = loops.peek().getStartIndex();
                                i = loops.pop().getEnd();
                            }
                        }
                        else {
                            i = selects.peek().getNext(i);
                        }
                    }
                }

                if (op.isLoopEnd()) {
                    if(loops.empty()) {
                        throw new PatternSearchException(this.name, pattern, i, "Unexpected loop end");
                    }
                    if (!res.isSuccess()) {
                        throw new NullPointerException();
                    }
                    else if (loops.peek().getEnd() == i){
                        //If at the loop end index, jump to start
                        loops.peek().setStartIndex(position);
                        i = loops.peek().getStart();
                    }
                }
                if (i == pattern.length - 1 && !loops.empty()) {
                    throw new PatternSearchException(this.name, pattern, i, "Unclosed loop");
                }

                ++i;
            }
            if (success) {
                
                //Assign identifier types
                Pattern p = Pattern.compile(pack.getIdentifierCode() + ".[0-9]*");
                Pattern tp = Pattern.compile("[0-9]*@\\$type");
                for (String name : types.keySet()) {
                    if (vars.get(name) == null) {
                        throw new PatternSearchException(name, pattern, index, "Variable " + name + " not found");
                    }
                    Matcher m = p.matcher(vars.get(name));
                    Matcher tm = tp.matcher(types.get(name));
        
                    String type = types.get(name);
                    if (tm.find()) {
                        type = tm.group().split("@")[0];
                    }
        
                    while (m.find()) {
                        int code = pack.extractIdentifier(m.group());
                        pack.setTypeOfId(code, type);
                    }
                }
                
                return new OperationResult(index, position, true, buildOutString(getName(), vars), error);
            }
        }
        return new OperationResult(index, index, false, buildOutString(getName(), vars), error);
    }
    
    private String buildOutString(String name, Map<String, String> vars) {
        StringBuilder out = new StringBuilder();
        out.append("(");
    
        int i = 0;
        for (String var : vars.keySet()) {
            
            out.append(vars.get(var)).append("@").append(var);
            
            if (i < vars.size() - 1) {
                out.append("|");
            }
            
            i++;
        }
        
        out.append("#").append(name).append(")");
        return out.toString();
    }

    /**
     * Performs specified syntax operation on data at index
     * @param op Operation to perform
     * @param data Input data sa an array of strings
     * @param index Index to perform operation at
     * @return Returns an object containing resulting information
     * */
    @NotNull
    @Contract("_, _, _ -> new")
    private OperationResult performOperation(@NotNull SyntaxOperation op, @NotNull String[] data, int index) throws PatternSearchException {
        //Checks if to perform symbol search
        if (op.isSymbol()) {
            
            //Checks if symbol is identifier or literal
            if (op.isIdentifier() || op.isLiteral()) {
                if (data[index].contains(".")) {
                    
                    String key = data[index].substring(0, data[index].indexOf('.'));
                    
                    if (op.isIdentifier() && key.equals(Integer.toString(this.pack.getIdentifierCode()))) {
                       return new OperationResult(index, index + 1, true, data[index], null);
                    }
                    else if (op.isLiteral() && key.equals(Integer.toString(this.pack.getLiteralCode()))) {
                        return new OperationResult(index, index + 1, true, data[index], null);
                    }
                    else {
                        return new OperationResult(index, index, false, "", new OperationResult.SyntaxError("Expected identifier or literal, but found: " + data[index] + " at " + index, data[index], index));
                    }
                }
                else {
                    return new OperationResult(index, index, false, "", new OperationResult.SyntaxError("Expected identifier or literal, but found: " + data[index] + " at " + index, data[index], index));
                }
            }

            //Checks if symbol exists
            if (!this.pack.hasSyntaxSymbol(op.getData())) {
                throw new PatternSearchException(op.getData(), null, index, "Symbol does not exist: " + op.getData());
            }
            
            //Tries fo find symbol in data
            if (this.pack.getSyntaxSymbol(op.getData()).getTerm() != null) {
                String term = this.pack.getSyntaxSymbol(op.getData()).getTerm();

                if (data[index].equals(term)) {
                    return new OperationResult(index, index + 1, true, term, null);
                }
                else {
                    return new OperationResult(index, index, false, "", new OperationResult.SyntaxError("Expected terminal symbol " + term + " , but found: " + data[index] + " at " + index, data[index], index));
                }
            }
            else {
                OperationResult res = this.pack.getSyntaxSymbol(op.getData()).searchPatterns(data, index);
                return new OperationResult(index, res.getNewPosition(), res.isSuccess(), res.toString(), res.getError());
            }
        }
        return new OperationResult(index, index, true, "", null);
    }
    
    @Contract(pure = true)
    String getTerm() {
        return this.term;
    }
    
    @NotNull
    @Contract("_, _, _ -> new")
    private LoopData findLoop(int startIndex, @NotNull SyntaxOperation[] pattern, int index) throws PatternSearchException {
        if (!pattern[index].isLoopStart()) {
            throw new PatternSearchException(this.name, pattern, index, "Loop start not found");
        }
        
        int l = 0;
        
        for (int i = index + 1; i < pattern.length; ++i) {
            SyntaxOperation op = pattern[i];
            
            if (op.isLoopStart()) {
                ++l;
            }
            if (op.isLoopEnd()) {
                if (l == 0) {
                    return new LoopData(startIndex, index, i);
                }
                --l;
            }
        }
        throw new PatternSearchException(this.name, pattern, pattern.length - 1, "Unclosed loop");
    }
    
    @NotNull
    @Contract("_, _, _ -> new")
    private SelectData findSelect(int startIndex, @NotNull SyntaxOperation[] pattern, int index) throws PatternSearchException {
        if (!pattern[index].isSelectionStart()) {
            throw new PatternSearchException(this.name, pattern, index, "Selection start not found");
        }
        
        int l = 0;
        SelectData data = new SelectData(startIndex, index);
        
        for (int i = index + 1; i < pattern.length; ++i) {
            SyntaxOperation op = pattern[i];
            
            if (op.isSelectionStart()) {
                ++l;
            }
            if (op.isSelectionBody()) {
                if (l == 0) {
                    data.addPoint(i);
                }
            }
            if (op.isSelectionEnd()) {
                if (l == 0) {
                    data.addPoint(i);
                    return data;
                }
                --l;
            }
        }
        throw new PatternSearchException(this.name, pattern, pattern.length - 1, "Unclosed selection");
    }
    
    public boolean isInlinePrecedence() {
        return inlinePrecedence;
    }
    
    public void setInlinePrecedence(boolean inlinePrecedence) {
        this.inlinePrecedence = inlinePrecedence;
    }
    
    static class LoopData {
        private int startIndex;
        private int start;
        private int end;
        
        LoopData(int startIndex, int start, int end) {
            this.startIndex = startIndex;
            this.start = start;
            this.end = end;
        }
        
        void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }
        
        int getStartIndex() {
            return startIndex;
        }
        
        int getStart() {
            return start;
        }
        
        int getEnd() {
            return end;
        }
    }
    
    static class SelectData {
        private int startIndex;
        private List<Integer> data = new ArrayList<>();
        
        SelectData(int startIndex, int start) {
            this.startIndex = startIndex;
            this.data.add(start);
        }
        
        int getStartIndex() {
            return startIndex;
        }
        
        int getStart() {
            return data.get(0);
        }
        
        int getEnd() {
            return data.get(data.size() - 1);
        }
        
        void addPoint(int index) {
            if (index <= this.getEnd()) {
                throw new IllegalArgumentException("New point position: " + index + " should be higher than previous: " + this.getEnd());
            }
            data.add(index);
        }
        
        Integer getNext(int index) {
            for (Integer aData : data) {
                if (aData > index) {
                    return aData;
                }
            }
            return null;
        }
        
        boolean hasPoint(int index) {
            for (int i : data) {
                if (i == index) {
                    return true;
                }
            }
            return false;
        }
    }
}
