package syntax;

import lexis.PascalSymbolPack;

/**Pascal syntax rules - simplified*/
public class PascalSimpleSyntaxPack extends SyntaxPack {
    
    @Override
    protected void init() {
        
        setSymbolPack(new PascalSymbolPack());
        //addTerminalsFromPack(new PascalSymbolPack());
        
        SyntaxOperation[][] patterns;
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("header", "s $header"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("description section", "s $desc"),
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("operator section", "s $operators"),
                new SyntaxOperation(".", "s")
        }};
        addSyntaxSymbol("main", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("program", "s type->$name"),
                new SyntaxOperation(null, "s id $name")
        }};
        addSyntaxSymbol("header", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("var", "s"),
                new SyntaxOperation("variable list", "s $list"),
                new SyntaxOperation(":", "s"),
                new SyntaxOperation("variable type", "s $type type->$list"),
                new SyntaxOperation("description section loop", "s $extra")
        }};
        addSyntaxSymbol("description section", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("variable list", "s $list"),
                new SyntaxOperation(":", "s"),
                new SyntaxOperation("variable type", "s $type type->$list"),
                new SyntaxOperation("description section loop", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("description section loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("integer", "s $type")
        }, {
                new SyntaxOperation("real", "s $type")
        }, {
                new SyntaxOperation("char", "s $type")
        }, {
                new SyntaxOperation("string", "s $type")
        }};
        addSyntaxSymbol("variable type", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id $var"),
                new SyntaxOperation("variable list loop", "s $extra"),
        }};
        addSyntaxSymbol("variable list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(",", "s"),
                new SyntaxOperation(null, "s id $var"),
                new SyntaxOperation("variable list loop", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("variable list loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("begin", "s"),
                new SyntaxOperation("operator list", "s $list"),
                new SyntaxOperation("end", "s")
        }, {
                new SyntaxOperation("operator", "s $op")
        }};
        addSyntaxSymbol("operator section", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("operator", "s $op"),
                new SyntaxOperation("operator list loop", "s $extra")
        }};
        addSyntaxSymbol("operator list", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(";", "s"),
                new SyntaxOperation("operator", "s $op"),
                new SyntaxOperation("operator list loop", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("operator list loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("assignment", "s $op")
        }, {
                new SyntaxOperation("conditional", "s $op")
        }, /*{
                new SyntaxOperation("loop", "s $op")
        },*/ {
                new SyntaxOperation("input", "s $op")
        }, {
                new SyntaxOperation("output", "s $op")
        }};
        addSyntaxSymbol("operator", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("read", "s"),
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("variable list", "s $list"),
                new SyntaxOperation(")", "s")
        }};
        addSyntaxSymbol("input", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("write", "s"),
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("variable list", "s $list"),
                new SyntaxOperation(")", "s")
        }};
        addSyntaxSymbol("output", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id $var"),
                new SyntaxOperation(":=", "s"),
                new SyntaxOperation("expression", "s $expr")
        }};
        addSyntaxSymbol("assignment", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("not", "s"),
                new SyntaxOperation("expression", "s $expr")
        }, {
                new SyntaxOperation("logic expression", "s $expr"),
                new SyntaxOperation("expression loop", "s $extra"),
                
        }};
        addSyntaxSymbol("expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("logic operator", "s $op"),
                new SyntaxOperation("logic expression", "s $expr"),
                new SyntaxOperation("expression loop", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("expression loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("and", "s $op")
        }, {
                new SyntaxOperation("or", "s $op")
        }};
        addSyntaxSymbol("logic operator", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("not", "s"),
                new SyntaxOperation("logic expression", "s $expr")
        }, {
                new SyntaxOperation("comparison", "s $expr"),
                new SyntaxOperation("logic expression loop", "s $extra")
        }};
        addSyntaxSymbol("logic expression", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("comparison operator", "s $op"),
                new SyntaxOperation("comparison", "s $expr"),
                new SyntaxOperation("logic expression loop", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("logic expression loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("=", "s $op")
        }, {
                new SyntaxOperation("<>", "s $op")
        }, {
                new SyntaxOperation("<", "s $op")
        }, {
                new SyntaxOperation("<=", "s $op")
        }, {
                new SyntaxOperation(">", "s $op")
        }, {
                new SyntaxOperation(">=", "s $op")
        }};
        addSyntaxSymbol("comparison operator", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("addition", "s $expr"),
                new SyntaxOperation("comparison loop plus", "s $extra")
        }, {
                new SyntaxOperation("comparison", "s $expr"),
                new SyntaxOperation("comparison loop minus", "s $extra")
        }};
        addSyntaxSymbol("comparison", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("+", "s $op"),
                new SyntaxOperation("addition", "s $expr"),
                new SyntaxOperation("comparison loop plus", "s $extra")
        }, {
                new SyntaxOperation("+", "s $op"),
                new SyntaxOperation("addition", "s $expr"),
                new SyntaxOperation("comparison loop minus", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("comparison loop plus", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("-", "s $op"),
                new SyntaxOperation("addition", "s $expr"),
                new SyntaxOperation("comparison loop plus", "s $extra")
        }, {
                new SyntaxOperation("-", "s $op"),
                new SyntaxOperation("addition", "s $expr"),
                new SyntaxOperation("comparison loop minus", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("comparison loop minus", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop multi", "s $extra")
        }, {
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop div", "s $extra")
        }, {
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop mod", "s $extra")
        }};
        addSyntaxSymbol("addition", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("*", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop multi", "s $extra")
        }, {
                new SyntaxOperation("*", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop div", "s $extra")
        }, {
                new SyntaxOperation("*", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop mod", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("addition loop multi", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("div", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop multi", "s $extra")
        }, {
                new SyntaxOperation("div", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop div", "s $extra")
        }, {
                new SyntaxOperation("div", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop mod", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("addition loop div", patterns, null);
    
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("mod", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop multi", "s $extra")
        }, {
                new SyntaxOperation("mod", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop div", "s $extra")
        }, {
                new SyntaxOperation("mod", "s $op"),
                new SyntaxOperation("multiplication", "s $expr"),
                new SyntaxOperation("addition loop mod", "s $extra")
        }, {
        
        }};
        addSyntaxSymbol("addition loop mod", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation(null, "s id $val")
        }, {
                new SyntaxOperation(null, "s lit $val")
        }, {
                new SyntaxOperation("(", "s"),
                new SyntaxOperation("expression", "s $val"),
                new SyntaxOperation(")", "s")
        }};
        addSyntaxSymbol("multiplication", patterns, null);
        
        /*patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("for", "s"),
                new SyntaxOperation("loop cond", "s $cond"),
                new SyntaxOperation("do", "s"),
                new SyntaxOperation("operator section", "s $operators")
        }};
        addSyntaxSymbol("loop", patterns, null);
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("assignment", "s $assign"),
                new SyntaxOperation("to", "s $op"),
                new SyntaxOperation("expression", "s $expr")
        }, {
                new SyntaxOperation("assignment", "s $assign"),
                new SyntaxOperation("downto", "s $op"),
                new SyntaxOperation("expression", "s $expr")
        }};
        addSyntaxSymbol("loop cond", patterns, null);*/
        
        patterns = new SyntaxOperation[][]{{
                new SyntaxOperation("if", "s"),
                new SyntaxOperation("expression", "s $cond"),
                new SyntaxOperation("then", "s"),
                new SyntaxOperation("operator section", "s $true"),
                new SyntaxOperation("else", "s"),
                new SyntaxOperation("operator section", "s $false")
        }, {
                new SyntaxOperation("if", "s"),
                new SyntaxOperation("expression", "s $cond"),
                new SyntaxOperation("then", "s"),
                new SyntaxOperation("operator section", "s $true")
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

