package generator;

import core.Lexer;
import lexis.SymbolPack;
import syntax.SyntaxPack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CodeGenerator {
    protected SymbolPack symbols;
    protected SyntaxPack syntax;
    protected Lexer lexer;
    
    protected Map<String, Function<SymbolData, String>> functions = new HashMap<>();
    
    public CodeGenerator(SymbolPack symbols, SyntaxPack syntax, Lexer lexer) {
        this.symbols = symbols;
        this.syntax = syntax;
        this.lexer = lexer;
        
        init();
    }
    
    protected String apply(SymbolData symbol) {
        if (symbol == null) {
            return "";
        }
        Function<SymbolData, String> func = functions.get(symbol.getName());
        if (func == null) {
            return symbol.getName();
        }
        return func.apply(symbol);
    }
    
    protected Function<SymbolData, String> add(String key, Function<SymbolData, String> function) {
        return functions.put(key, function);
    }
    
    protected abstract void init();
    
    public abstract String generate(SymbolData data);
}
