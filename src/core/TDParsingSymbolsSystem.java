package core;

import lexis.SymbolPack;
import syntax.OperationResult;
import syntax.PatternSearchException;
import syntax.SyntaxPack;
import syntax.SyntaxSymbol;

/**Recursive parser*/
public class TDParsingSymbolsSystem extends SymbolsSystem implements Parser {
    protected SyntaxPack syntax;
    
    public TDParsingSymbolsSystem(SymbolPack symbolPack, SyntaxPack syntaxPack) {
        super(symbolPack);
        syntax = syntaxPack;
        syntaxPack.setSystem(this);
    }
    
    @Override
    public OperationResult process(String[] input) throws PatternSearchException {
        SyntaxSymbol mainSymbol = syntax.getSyntaxSymbol(syntax.getMainSymbol());
        return mainSymbol.searchPatterns(input, 0);
    }
}
