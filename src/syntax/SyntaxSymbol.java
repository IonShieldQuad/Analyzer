package syntax;

import core.Logger;

import java.util.Arrays;
import java.util.Stack;

public class SyntaxSymbol {
    private final SyntaxPack pack;
    private final String name;
    private final SyntaxOperation[][] patterns;
    private final String term;
    private String buffer;
    private String output;

    public SyntaxSymbol(SyntaxPack pack, String name, SyntaxOperation[][] patterns, String term) {
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

    public String getOutput() {
        return this.output;
    }

    /**
     * Perform search of patterns in data starting at index
     * @param data Input data sa an array of strings
     * @param index Index to begin search at
     * @return If search succeeded, returns new index, null otherwise
     * */
    public Integer searchPatterns(String[] data, int index) throws PatternSearchException {
        this.output = null;
        for (SyntaxOperation[] pattern : patterns) {
            int position = index;
            boolean success = true;
            //TODO: Add output

            StringBuilder out = new StringBuilder();
            Stack<LoopData> loops = new Stack<>();
            Stack<SelectData>selects = new Stack<>();

            for (int i = 0; i < pattern.length;) {
                SyntaxOperation op = pattern[i];

                if (op.isLoopStart() && (loops.isEmpty() || loops.peek().getStart() != i)) {
                    loops.push(findLoop(position, pattern, i));
                }

                if (op.isSelectionStart() && (selects.isEmpty() || selects.peek().getStart() != i)) {
                    selects.push(findSelect(position, pattern, i));
                }
    
                System.out.println(name + ": " + i + " enter");
                Logger.getInstance().logln(name + ": " + i + " enter");
                
                Integer res = performOperation(op, data, position);
                
                System.out.println("(" + data[position] + ") " + position + " -> " + res + (loops.isEmpty() ? "" : " l") + (selects.isEmpty() ? "" : " s") + " : " + name + ": " + i + " exit");
                Logger.getInstance().logln("(" + data[position] + ") " + position + " -> " + res + (loops.isEmpty() ? "" : " l") + (selects.isEmpty() ? "" : " s") + " : " + name + ": " + i + " exit");
                
                if (res == null && loops.isEmpty() && selects.isEmpty()) {
                    success = false;
                    break;
                }

                if (res != null) {
                    position = res;
                    if (buffer.length() != 0) {
                        if (!buffer.startsWith(" ") && !out.toString().endsWith(" ")) {
                            out.append(" ");
                        }
                        out.append(buffer);
                        if (!buffer.endsWith(" ") && !out.toString().startsWith(" ")) {
                            out.append(" ");
                        }
                    }
                    
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
                        position = selects.peek().getStartIndex();
                        if (selects.peek().getEnd() == selects.peek().getNext(i)) {
                            
                            selects.pop();
                            
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
                    if (res == null) {
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
                output = out.toString();
                return position;
            }
        }
        return null;
    }

    /**
     * Performs specified syntax operation on data at index
     * @param op Operation to perform
     * @param data Input data sa an array of strings
     * @param index Index to perform operation at
     * @return If operation succeeded, returns new index, null otherwise
     * */
    private Integer performOperation(SyntaxOperation op, String[] data, int index) throws PatternSearchException {
        //Checks if to perform symbol search
        if (op.isSymbol()) {
            
            //Checks if symbol is identifier or literal
            if (op.isIdentifier() || op.isLiteral()) {
                if (data[index].contains(".")) {
                    
                    String key = data[index].substring(0, data[index].indexOf('.'));
                    
                    if (op.isIdentifier() && key.equals(Integer.toString(this.pack.getIdentifierCode()))) {
                       this.buffer = data[index];
                       return index + 1;
                    }
                    else if (op.isLiteral() && key.equals(Integer.toString(this.pack.getLiteralCode()))) {
                        this.buffer = data[index];
                        return index + 1;
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }

            if (!this.pack.hasSyntaxSymbol(op.getData())) {
                throw new PatternSearchException(op.getData(), null, index, "Symbol does not exist: " + op.getData());
            }
            
            if (this.pack.getSyntaxSymbol(op.getData()).getTerm() != null) {
                String term = this.pack.getSyntaxSymbol(op.getData()).getTerm();

                if (data[index].equals(term)) {
                    this.buffer = term;
                    return index + 1;
                }
                else {
                    return null;
                }
            }
            else {
                Integer i = this.pack.getSyntaxSymbol(op.getData()).searchPatterns(data, index);
                buffer = this.pack.getSyntaxSymbol(op.getData()).getOutput();
                return i;
            }
        }
        buffer = "";
        return index;
    }

    public String getTerm() {
        return this.term;
    }
    
    private LoopData findLoop(int startIndex, SyntaxOperation[] pattern, int index) throws PatternSearchException {
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
    
    private SelectData findSelect(int startIndex, SyntaxOperation[] pattern, int index) throws PatternSearchException {
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
}
