package core;

public class IdData {
    private String type = "NO_VALUE";
    private String name;
    private int key;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getKey() {
        return key;
    }
    
    public void setKey(int key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return key + ":" + name + ":" + type;
    }
}
