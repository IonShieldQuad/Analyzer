package core;

import lexis.SymbolPack;
import syntax.OperationResult;
import syntax.PrecedenceTable;
import syntax.SyntaxPack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Operator precedence parser*/
public class BUParsingSymbolSystem extends SymbolsSystem implements Parser {
    protected SyntaxPack syntax;
    private PrecedenceTable table;
    
    BUParsingSymbolSystem(SymbolPack symbolPack, SyntaxPack syntaxPack, PrecedenceTable table) {
        super(symbolPack);
        syntax = syntaxPack;
        this.table = table;
        syntaxPack.setSystem(this);
    }
    
    @Override
    public OperationResult process(String[] input) {
        List<String> data = new ArrayList<>(Arrays.asList(input));
        List<String> res = new ArrayList<>();
        
        int k = data.size() - 1;
        
        do {
            int i = 0;
            String n1;
            String n2;
            do {
                i++;
                n1 = data.get(i - 1);
                n2 = data.get(i);
            } while (table.get(n1, n2) != PrecedenceTable.Precedence.HIGHER && i < k);
            
            int j2 = i - 1;
            int j1 = i;
            
            do {
                j1--;
                n1 = data.get(j1);
                n2 = data.get(j1 + 1);
            } while (table.get(n1, n2) != PrecedenceTable.Precedence.EQUAL && j1 > 0);
    
            for (int x = j1 + 1; x <= j2; x++) {
                res.add(data.get(x));
            }
            
            int j = j1;
            
            for (int x = i; x <= k; x++) {
                j++;
                data.set(j, data.get(x));
            }
            
            k -= (j2 - j1);
        } while (k > 1);
        
        String r = res.stream().reduce("", (a, b) -> a + " " + b);
        return new OperationResult(0, input.length - 1, true, r, null);
    }
}
