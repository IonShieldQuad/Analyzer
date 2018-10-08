package syntax;

import java.util.ArrayList;
import java.util.List;

public class SelectData {
    private int startIndex;
    private List<Integer> data = new ArrayList<>();
    
    SelectData(int startIndex, int start) {
        this.startIndex = startIndex;
        this.data.add(start);
    }
    
    public int getStartIndex() {
        return startIndex;
    }
    
    public int getStart() {
        return data.get(0);
    }
    
    public int getEnd() {
        return data.get(data.size() - 1);
    }
    
    public boolean addPoint(int index) {
        if (index <= this.getEnd()) {
            return false;
        }
        data.add(index);
        return true;
    }
    
    public Integer getNext(int index) {
        for (Integer aData : data) {
            if (aData > index) {
                return aData;
            }
        }
        return null;
    }
    
    public boolean hasPoint(int index) {
        for (int i : data) {
            if (i == index) {
                return true;
            }
        }
        return false;
    }
}
