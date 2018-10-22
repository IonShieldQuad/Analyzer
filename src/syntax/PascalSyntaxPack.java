package syntax;

import lexis.PascalSymbolPack;

public class PascalSyntaxPack extends SyntaxPack {
    
    @Override
    protected void init() {
        
        addTerminalsFromPack(new PascalSymbolPack());
        
        SyntaxOperation[][] patterns;
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("header", "s"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("description section", "s"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("operator section", "s"),
                new SyntaxOperation(".", "s")
        }};
        addSyntaxSymbol("main", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("program", "s"),
                new SyntaxOperation("program name", "s")
        }};
        addSyntaxSymbol("header", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id")
        }};
        addSyntaxSymbol("program name", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("var", "s"),
                new SyntaxOperation("variable list", "s"),
                new SyntaxOperation(":", "s"),
                new SyntaxOperation("variable type", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("variable list", "s"),
                new SyntaxOperation(":", "s"),
                new SyntaxOperation("variable type", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("description section", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("integer", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("real", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("char", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("string", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("variable type", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation(",", "s"),
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("variable list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("begin", "s"),
                new SyntaxOperation("operator list", "s"),
                new SyntaxOperation("end", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("operator", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("operator section", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("operator", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("operator", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("operator list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("assignment", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("conditional", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("loop", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("input", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("output", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("operator", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("read", "s"),
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("variable list", "s"),
                new SyntaxOperation(")", "s")
        }};
        addSyntaxSymbol("input", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("write", "s"),
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("variable list", "s"),
                new SyntaxOperation(")", "s")
        }};
        addSyntaxSymbol("output", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation(":=", "s"),
                new SyntaxOperation("expression", "s")
        }};
        addSyntaxSymbol("assignment", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("not", "s"),
                new SyntaxOperation("expression", "s")
        },
        {
                new SyntaxOperation("logic expression", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation("logic operator", "s"),
                new SyntaxOperation("logic expression", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("and", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("or", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("logic operator", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("not", "s"),
                new SyntaxOperation("logic expression", "s")
        },
        {
                new SyntaxOperation("comparison", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation("comparison operator", "s"),
                new SyntaxOperation("comparison", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("logic expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("=", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("<>", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("<", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("<=", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation(">", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation(">=", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("comparison operator", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("+", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("-", "s"),
                new SyntaxOperation(null, "se"),
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("comparison", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation(null, "ls"),
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("*", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("div", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("mod", "s"),
                new SyntaxOperation(null, "se"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation(null, "le")
        }};
        addSyntaxSymbol("addition", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation(null, "s lit"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("expression", "s"),
                new SyntaxOperation(")", "s"),
                new SyntaxOperation(null, "se")
        }};
        addSyntaxSymbol("multiplication", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("for", "s"),
                new SyntaxOperation("loop body", "s"),
                new SyntaxOperation("do", "s"),
                new SyntaxOperation("operator section", "s")
        }};
        addSyntaxSymbol("loop", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("assignment", "s"),
                new SyntaxOperation(null, "ss"),
                new SyntaxOperation("to", "s"),
                new SyntaxOperation(null, "sb"),
                new SyntaxOperation("downto", "s"),
                new SyntaxOperation(null, "se"),
                new SyntaxOperation("expression", "s")
        }};
        addSyntaxSymbol("loop body", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("if", "s"),
                new SyntaxOperation("expression", "s"),
                new SyntaxOperation("then", "s"),
                new SyntaxOperation("operator section", "s"),
                new SyntaxOperation("else", "s"),
                new SyntaxOperation("operator section", "s")
        },
        {
                new SyntaxOperation("if", "s"),
                new SyntaxOperation("expression", "s"),
                new SyntaxOperation("then", "s"),
                new SyntaxOperation("operator section", "s")
        }};
        addSyntaxSymbol("conditional", patterns, null);
        
        /*
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, ""),
        }};
        addSyntaxSymbol("", patterns, null);
        */
        
        
        setMainSymbol("main");
    }
}
