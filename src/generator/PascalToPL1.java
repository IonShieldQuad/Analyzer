package generator;

import core.Lexer;
import lexis.SymbolPack;
import syntax.SyntaxPack;

public class PascalToPL1 extends CodeGenerator {
    
    public PascalToPL1(SymbolPack symbols, SyntaxPack syntax, Lexer lexer) {
        super(symbols, syntax, lexer);
    }
    
    @Override
    protected void init() {
    
    }
    
    @Override
    public String generate(SymbolData data) {
        return apply(data);
    }
}
