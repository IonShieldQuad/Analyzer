package syntax;

import lexis.PascalSymbolPack;

public class PascalSimpleSyntaxPack extends SyntaxPack {
    
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
                new SyntaxOperation("description section loop", "s")
        }};
        addSyntaxSymbol("description section", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("variable list", "s"),
                new SyntaxOperation(":", "s"),
                new SyntaxOperation("variable type", "s"),
                new SyntaxOperation("description section loop", "s")
        }, {
        
        }};
        addSyntaxSymbol("description section loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("integer", "s")
        }, {
                new SyntaxOperation("real", "s")
        }, {
                new SyntaxOperation("char", "s")
        }, {
                new SyntaxOperation("string", "s")
        }};
        addSyntaxSymbol("variable type", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation("variable list loop", "s"),
        }};
        addSyntaxSymbol("variable list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(",", "s"),
                new SyntaxOperation(null, "s id"),
                new SyntaxOperation("variable list loop", "s")
        }, {
        
        }};
        addSyntaxSymbol("variable list loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("begin", "s"),
                new SyntaxOperation("operator list", "s"),
                new SyntaxOperation("end", "s")
        }, {
                new SyntaxOperation("operator", "s")
        }};
        addSyntaxSymbol("operator section", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("operator", "s"),
                new SyntaxOperation("operator list loop", "s")
        }};
        addSyntaxSymbol("operator list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("operator", "s"),
                new SyntaxOperation("operator list loop", "s")
        }, {
        
        }};
        addSyntaxSymbol("operator list loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("assignment", "s")
        }, {
                new SyntaxOperation("conditional", "s")
        }, {
                new SyntaxOperation("loop", "s")
        }, {
                new SyntaxOperation("input", "s")
        }, {
                new SyntaxOperation("output", "s")
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
        }, {
                new SyntaxOperation("logic expression", "s"),
                new SyntaxOperation("expression loop", "s"),
                
        }};
        addSyntaxSymbol("expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("logic operator", "s"),
                new SyntaxOperation("logic expression", "s"),
                new SyntaxOperation("expression loop", "s")
        }, {
        
        }};
        addSyntaxSymbol("expression loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("and", "s")
        }, {
                new SyntaxOperation("or", "s")
        }};
        addSyntaxSymbol("logic operator", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("not", "s"),
                new SyntaxOperation("logic expression", "s")
        }, {
                new SyntaxOperation("comparison", "s"),
                new SyntaxOperation("logic expression loop", "s")
        }};
        addSyntaxSymbol("logic expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("comparison operator", "s"),
                new SyntaxOperation("comparison", "s"),
                new SyntaxOperation("logic expression loop", "s")
        }, {
        
        }};
        addSyntaxSymbol("logic expression loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("=", "s")
        }, {
                new SyntaxOperation("<>", "s")
        }, {
                new SyntaxOperation("<", "s")
        }, {
                new SyntaxOperation("<=", "s")
        }, {
                new SyntaxOperation(">", "s")
        }, {
                new SyntaxOperation(">=", "s")
        }};
        addSyntaxSymbol("comparison operator", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation("comparison loop plus", "s")
        }, {
                new SyntaxOperation("comparison", "s"),
                new SyntaxOperation("comparison loop minus", "s")
        }};
        addSyntaxSymbol("comparison", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("+", "s"),
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation("comparison loop plus", "s")
        }, {
                new SyntaxOperation("+", "s"),
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation("comparison loop minus", "s")
        }, {
        
        }};
        addSyntaxSymbol("comparison loop plus", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("-", "s"),
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation("comparison loop plus", "s")
        }, {
                new SyntaxOperation("-", "s"),
                new SyntaxOperation("addition", "s"),
                new SyntaxOperation("comparison loop minus", "s")
        }, {
        
        }};
        addSyntaxSymbol("comparison loop minus", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop multi", "s")
        }, {
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop div", "s")
        }, {
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop mod", "s")
        }};
        addSyntaxSymbol("addition", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("*", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop multi", "s")
        }, {
                new SyntaxOperation("*", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop div", "s")
        }, {
                new SyntaxOperation("*", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop mod", "s")
        }, {
        
        }};
        addSyntaxSymbol("addition loop multi", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("div", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop multi", "s")
        }, {
                new SyntaxOperation("div", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop div", "s")
        }, {
                new SyntaxOperation("div", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop mod", "s")
        }, {
        
        }};
        addSyntaxSymbol("addition loop div", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("mod", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop multi", "s")
        }, {
                new SyntaxOperation("mod", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop div", "s")
        }, {
                new SyntaxOperation("mod", "s"),
                new SyntaxOperation("multiplication", "s"),
                new SyntaxOperation("addition loop mod", "s")
        }, {
        
        }};
        addSyntaxSymbol("addition loop mod", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id")
        }, {
                new SyntaxOperation(null, "s lit")
        }, {
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("expression", "s"),
                new SyntaxOperation(")", "s")
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
                new SyntaxOperation("to", "s"),
                new SyntaxOperation("expression", "s")
        }, {
                new SyntaxOperation("assignment", "s"),
                new SyntaxOperation("downto", "s"),
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
        }, {
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

