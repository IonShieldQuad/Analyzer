package syntax;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntaxOperation {
    private final String data;
    private final String[] params;

    public SyntaxOperation(@Nullable String data, @Nullable String params) {
        this.data = data;
        if (params != null) {
            this.params = params.split(" ");
        }
        else {
            this.params = new String[0];
        }
    }

    String getData() {
        return data;
    }

    public String[] getParams() {
        return params.clone();
    }

    boolean isSymbol() {
        return contains("s");
    }

    boolean isIdentifier() {
        return contains("id");
    }

    boolean isLiteral() {
        return contains("lit");
    }

    boolean isLoopStart() {
        return contains("ls");
    }

    boolean isLoopEnd() {
        return contains("le");
    }

    boolean isSelectionStart() {
        return contains("ss");
    }

    boolean isSelectionEnd() {
        return contains("se");
    }

    boolean isSelectionBody() {
        return contains("sb");
    }
    
    boolean isIdType() {
        return Arrays.stream(params).anyMatch(s -> s.startsWith("type->$"));
    }
    
    public List<String> idsTypeList() {
        return Arrays.stream(params).filter(s -> s.startsWith("type->$")).map(s -> s.substring(6)).collect(ArrayList::new, List::add, List::addAll);
    }
    
    @Contract(pure = true)
    private boolean contains(String param) {
        return Arrays.asList(params).contains(param);
    }
    
    @Contract(pure = true)
    public List<String> getVariables() {
        /*List<String> list = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            if (params[i].length() > 0 && params[i].startsWith("$")) {
                list.add(params[i]);
            }
        }*/
        return Arrays.stream(params).filter(s -> !s.equals("") && s.startsWith("$")).collect(ArrayList::new, List::add, List::addAll);
        /*return list;*/
    }
    
    @Contract(pure = true)
    public boolean containsVariable(String var) {
        return Arrays.stream(params).filter((s) -> s.startsWith("$")).anyMatch((s) -> s.equals(var));
    }
    
    @Contract(pure = true)
    public boolean containsVariables() {
        return !getVariables().isEmpty();
    }
}
