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
    
    @Contract(pure = true)
    private boolean contains(String param) {
        return Arrays.asList(params).contains(param);
    }
    
    @Contract(pure = true)
    public List<String> getVariables() {
        return Arrays.stream(params).filter((s) -> s.startsWith("$")).collect(ArrayList<String>::new, List::add, List::addAll);
    }
    
    @Contract(pure = true)
    public boolean containsVariable(String var) {
        return Arrays.stream(params).filter((s) -> s.startsWith("$")).anyMatch((s) -> s.equals(var));
    }
    
    @Contract(pure = true)
    public boolean containsVariables() {
        return getVariables().isEmpty();
    }
}
