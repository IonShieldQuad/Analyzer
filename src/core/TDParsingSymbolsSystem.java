package core;

import lexis.SymbolPack;
import syntax.OperationResult;
import syntax.PatternSearchException;
import syntax.SyntaxPack;
import syntax.SyntaxSymbol;

public class TDParsingSymbolsSystem extends SymbolsSystem implements Parser {
    protected SyntaxPack syntax;
    
    public TDParsingSymbolsSystem(SymbolPack symbolPack, SyntaxPack syntaxPack) {
        super(symbolPack);
        syntax = syntaxPack;
        
        for (String name : symbols.symbolSet()) {
            syntaxPack.addSyntaxSymbol(name, null, Integer.toString(symbols.find(name)));
        }
    
        syntaxPack.setIdentifierCode(symbolPack.getIdentifierCode());
        syntaxPack.setLiteralCode(symbolPack.getLiteralCode());
    }
    
    @Override
    public OperationResult process(String[] input) throws PatternSearchException {
        SyntaxSymbol mainSymbol = syntax.getSyntaxSymbol(syntax.getMainSymbol());
        return mainSymbol.searchPatterns(input, 0);
    }
}
