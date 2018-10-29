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
        
        List<List<SymbolListItem>> levels = new ArrayList<>();
        
        Stack<SyntaxPack.OperationPosition> stack = new Stack<>();
        //TODO: implement table generation
       
        stack.push(new SyntaxPack.OperationPosition(pack.getSyntaxSymbol(pack.getMainSymbol())));
        while(!stack.empty()) {
            
            if (stack.size() > 1000) {
                throw new StackOverflowError();
            }
            
            //If there is a valid symbol at current index
            if (stack.peek().patternIndex < stack.peek().symbol.getPatterns().length
            && stack.peek().operationIndex < stack.peek().symbol.getPatterns()[stack.peek().patternIndex].length) {
                //TODO:
                
                System.out.println(stack.peek().symbol.getName() + ": " + stack.peek().find().getData() + " : " + Arrays.stream(stack.peek().find().getParams()).reduce("", (a, b) -> a + " " + b));
                Logger.getInstance().logln("tableGen", stack.peek().symbol.getName() + ": " + stack.peek().find().getData() + " : " + Arrays.stream(stack.peek().find().getParams()).reduce("", (a, b) -> a + " " + b));
                
                //If current operation is symbol match
                if (stack.peek().find().isSymbol()) {
                    
                    //If current operation matches a not terminal symbol
                    if (stack.peek().find().isIdentifier() || stack.peek().find().isLiteral() || pack.getSyntaxSymbol(stack.peek().find().getData()).getTerm() != null) {
                        
                        String symbol;
                        if (stack.peek().find().isIdentifier()) {
                            symbol = Integer.toString(pack.getIdentifierCode());;
                        }
                        else if (stack.peek().find().isLiteral()) {
                            symbol = Integer.toString(pack.getLiteralCode());;
                        }
                        else {
                            symbol = pack.getSyntaxSymbol(stack.peek().find().getData()).getTerm();
                        }
                        
                        if (stack.peek().loops.empty()) {
                            
                            //Both loops and selects are empty
                            if (stack.peek().selects.empty()) {
                                for (int i = 0; i < levels.size(); ++i) {
                                    int l = i;
    
                                    if (i >= stack.size()) {
                                        levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        levels.get(l).clear();
                                    }
                                    else {
                                        if (stack.peek().termNum == 0) {
                                            levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        }
                                    }
    
                                }
    
                                //Adds this symbol to list
                                levels.get(stack.size()).add(new SymbolListItem(
                                        symbol,
                                        stack.peek().operationIndex,
                                        stack.peek().loops.isEmpty() ? null : stack.peek().loops.peek(),
                                        stack.peek().selects.isEmpty() ? null : stack.peek().selects.peek()
                                ));
    
                                stack.peek().operationIndex++;
                            }
                            //Loops are empty, selects are not
                            else {
                                for (int i = 0; i < levels.size(); ++i) {
                                    int l = i;
        
                                    if (i >= stack.size()) {
                                        levels.get(l).forEach((e) -> {
                                            if (e.selectData == null || e.selectData.getNext(e.position) > stack.peek().operationIndex) {
                                                tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size())));}
                                        });
                                        levels.get(l).clear();
                                    }
                                    else {
                                        if (stack.peek().termNum == 0) {
                                            levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        }
                                    }
        
                                }
    
                                //Adds this symbol to list
                                levels.get(stack.size()).add(new SymbolListItem(
                                        symbol,
                                        stack.peek().operationIndex,
                                        stack.peek().loops.isEmpty() ? null : stack.peek().loops.peek(),
                                        stack.peek().selects.isEmpty() ? null : stack.peek().selects.peek()
                                ));
                                stack.peek().operationIndex++;
                            }
                            
                        }
                        else {
                            //Loops are not empty, selects are
                            if (stack.peek().selects.empty()) {
                                for (int i = 0; i < levels.size(); ++i) {
                                    int l = i;
        
                                    if (i >= stack.size()) {
                                        levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        levels.get(l).clear();
                                    }
                                    else {
                                        if (stack.peek().termNum == 0) {
                                            levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        }
                                    }
        
                                }
    
                                //Adds this symbol to list
                                levels.get(stack.size()).add(new SymbolListItem(
                                        symbol,
                                        stack.peek().operationIndex,
                                        stack.peek().loops.isEmpty() ? null : stack.peek().loops.peek(),
                                        stack.peek().selects.isEmpty() ? null : stack.peek().selects.peek()
                                ));
                                stack.peek().operationIndex++;
                            }
                            //Both loops and selects are not empty
                            else {
                                for (int i = 0; i < levels.size(); ++i) {
                                    int l = i;
        
                                    if (i >= stack.size()) {
                                        levels.get(l).forEach((e) -> {
                                            if (e.selectData == null || e.selectData.getNext(e.position) > stack.peek().operationIndex) {
                                                tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size())));}
                                        });
                                        levels.get(l).clear();
                                    }
                                    else {
                                        if (stack.peek().termNum == 0) {
                                            levels.get(l).forEach((e) -> tuples.add(new PrecedenceTuple(e.name, symbol, calculatePrecedence(l, stack.size()))));
                                        }
                                    }
        
                                }
    
                                //Adds this symbol to list
                                levels.get(stack.size()).add(new SymbolListItem(
                                        symbol,
                                        stack.peek().operationIndex,
                                        stack.peek().loops.isEmpty() ? null : stack.peek().loops.peek(),
                                        stack.peek().selects.isEmpty() ? null : stack.peek().selects.peek()
                                ));
                                stack.peek().operationIndex++;
                            }
                            
                        }
                        
                        stack.peek().termNum++;
                    }
                    //Else pushes symbol on stack and updates list capacity if needed
                    else {
                        stack.push(new SyntaxPack.OperationPosition(pack.getSyntaxSymbol(stack.peek().find().getData())));
                        while (levels.size() <= stack.size()) {
                            levels.add(new ArrayList<>());
                        }
                    }
                    
                }
                else {
                    //If operation is loop start, push the loop on the stack
                    if (stack.peek().find().isLoopStart()) {
                        if (stack.peek().loops.empty() || stack.peek().loops.peek().getStart() != stack.peek().operationIndex) {
                            stack.peek().loops.push(stack.peek().symbol.findLoop(0, stack.peek().symbol.getPatterns()[stack.peek().patternIndex], stack.peek().operationIndex));
                        }
                    }
                    //If operation is select start, push the select on the stack
                    if (stack.peek().find().isSelectionStart()) {
                        if (stack.peek().selects.empty() || stack.peek().selects.peek().getStart() != stack.peek().operationIndex) {
                            stack.peek().selects.push(stack.peek().symbol.findSelect(0, stack.peek().symbol.getPatterns()[stack.peek().patternIndex], stack.peek().operationIndex));
                        }
                    }
                    //Pops loop from stack on end
                    if (stack.peek().find().isLoopEnd()) {
                        SyntaxSymbol.LoopData l = stack.peek().loops.pop();
                        if (l.getStartIndex() == 0) {
                            l.setStartIndex(1);
                            stack.peek().loops.push(l);
                        }
                        stack.peek().loops.pop();
                    }
                    if (stack.peek().find().isSelectionEnd()) {
                        stack.peek().selects.pop();
                    }
                    
                    stack.peek().operationIndex++;
                }
                
                
            }
            
            //Switches to another sequence if current has no more operations
            if (stack.peek().operationIndex >= stack.peek().symbol.getPatterns()[stack.peek().patternIndex].length) {
                stack.peek().patternIndex++;
                stack.peek().operationIndex = 0;
                stack.peek().termNum = 0;
            }
    
            //If there are no more sequences, pop symbol from stack
            if (stack.peek().patternIndex >= stack.peek().symbol.getPatterns().length) {
                stack.pop();
                if (!stack.empty()) {
                    stack.peek().operationIndex++;
                }
            }
            
        }
    
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
    
    private static class SymbolListItem {
        String name;
        int position;
        SyntaxSymbol.LoopData loopData;
        SyntaxSymbol.SelectData selectData;
        
        SymbolListItem(String name, int position) {
            this.name = name;
            this.position = position;
        }
    
        SymbolListItem(String name, int position, SyntaxSymbol.LoopData loopData, SyntaxSymbol.SelectData selectData) {
            this.name = name;
            this.position = position;
            this.loopData = loopData;
            this.selectData = selectData;
        }
    
        @Override
        public String toString() {
            return name;
        }
    }
}
