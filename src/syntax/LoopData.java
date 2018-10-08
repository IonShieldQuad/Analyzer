package syntax;

public class LoopData {
    private int startIndex;
    private int start;
    private int end;
    
    LoopData(int startIndex, int start, int end) {
        this.startIndex = startIndex;
        this.start = start;
        this.end = end;
    }
    
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
    
    public int getStartIndex() {
        return startIndex;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
}
