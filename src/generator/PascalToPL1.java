package generator;

import core.IdData;
import core.Lexer;
import lexis.SymbolPack;
import syntax.SyntaxPack;

import java.util.List;


public class PascalToPL1 extends CodeGenerator {
    
    public PascalToPL1(SymbolPack symbols, SyntaxPack syntax, Lexer lexer) {
        super(symbols, syntax, lexer);
    }
    
    @Override
    protected void init() {
        List<IdData> ids = lexer.getIdList();
        
        add("main", sd -> {
            String header =  apply(sd.get("header"));
            return header + ": PROCEDURE OPTIONS (MAIN);\r\n" + apply(sd.get("desc")) + apply(sd.get("operators")) + ";\r\n" + "END " + header + ";\r\n";
        });
        
        add("header", sd -> {
           String name = sd.get("name").getName();
           return ids.get(symbols.extractIdentifier(name)).getName().toUpperCase();
        });
        
        add("description section", sd -> {
            sd.get("list").getSymbols().put("type", sd.get("type"));
            return apply(sd.get("list")) + apply(sd.get("extra"));
        });
    
        add("description section loop", sd -> {
            if (sd.get("list") == null) return "";
            sd.get("list").getSymbols().put("type", sd.get("type"));
            return apply(sd.get("list")) + apply(sd.get("extra"));
        });
        
        add("variable list", sd -> {
            IdData idData = ids.get(symbols.extractIdentifier(sd.get("var").getName()));
            if (sd.get("extra") != null) {
                sd.get("extra").getSymbols().put("type", sd.get("type"));
            }
            if (sd.get("type") != null) {
                return "DECLARE " + idData.getName().toUpperCase() + " " + apply(sd.get("type")) + ";\r\n" + apply(sd.get("extra"));
            }
            return idData.getName().toUpperCase() + (sd.get("extra").get("var") == null ? "" : ", ") + apply(sd.get("extra"));
        });
        
        add("variable list loop", sd -> {
            if (sd.get("var") == null) return "";
            IdData idData = ids.get(symbols.extractIdentifier(sd.get("var").getName()));
            if (sd.get("extra") != null) {
                sd.get("extra").getSymbols().put("type", sd.get("type"));
            }
            if (sd.get("type") != null) {
                return "DECLARE " + idData.getName().toUpperCase() + " " + apply(sd.get("type")) + ";\r\n" + apply(sd.get("extra"));
            }
            return idData.getName().toUpperCase() + (sd.get("extra").get("var") == null ? "" : ", ") + apply(sd.get("extra"));
        });
        
        add("variable type", sd -> {
            String type = symbols.find(Integer.parseInt(sd.get("type").getName()));
            if ("integer".equals(type)) {
                return "FIXED DECIMAL(32, 0)";
            }
            if ("real".equals(type)) {
                return "FLOAT DECIMAL(32)";
            }
            if ("char".equals(type)) {
                return "CHARACTER(1)";
            }
            if ("string".equals(type)) {
                return "CHARACTER(32767) VARIABLE";
            }
            return "";
        });
        
        add("operator section", sd -> {
            if (sd.get("op") != null) {
                return apply(sd.get("op"));
            }
            if (sd.get("list") != null) {
                return "DO;\r\n" + apply(sd.get("list")) + "END";
            }
            return "";
        });
        
        add("operator list", sd -> apply(sd.get("op")) + ";\r\n" + apply(sd.get("extra")));
    
        add("operator list loop", sd -> {
            if (sd.get("op") == null) return "";
            return apply(sd.get("op")) + ";\r\n" + apply(sd.get("extra"));
        });
        
        add("operator", sd -> apply(sd.get("op")));
        
        add("input", sd -> "READ(" + apply(sd.get("list")) + ")");
    
        add("output", sd -> "WRITE(" + apply(sd.get("list")) + ")");
        
        add("assignment", sd -> ids.get(symbols.extractIdentifier(sd.get("var").getName())).getName().toUpperCase() + " = " + apply(sd.get("expr")));
        
        add("conditional", sd -> {
            if (sd.get("false") == null) {
                return "IF " + apply(sd.get("cond")) + "\r\n" + "THEN " + apply(sd.get("true"));
            }
            return "IF " + apply(sd.get("cond")) + "\r\n" + "THEN " + apply(sd.get("true")) + ";\r\nELSE " + apply(sd.get("false"));
        });
        
        add("expression", sd -> {
            if (sd.get("extra") == null) {
                return "^" + apply(sd.get("expr"));
            }
            return apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("expression loop", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            return apply(sd.get("op")) + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
        
        add("logic operator", sd -> {
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));
            if ("and".equals(op)) {
                return "&";
            }
            if ("or".equals(op)) {
                return "|";
            }
            return "";
        });
        
        add("logic expression", sd -> {
            if (sd.get("extra") == null) {
                return "^" + apply(sd.get("expr"));
            }
            return apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("logic expression loop", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            return apply(sd.get("op")) + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("comparison operator", sd -> {
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));
            if ("=".equals(op)) {
                return "=";
            }
            if ("<>".equals(op)) {
                return "^=";
            }
            if ("<".equals(op)) {
                return "<=";
            }
            if ("<=".equals(op)) {
                return "<=";
            }
            if (">".equals(op)) {
                return ">";
            }
            if (">=".equals(op)) {
                return ">=";
            }
            return "";
        });
        
        add("comparison", sd -> apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra"))));
        
        add("comparison loop plus", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));;
            return op + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("comparison loop minus", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));;
            return op + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
        
        add("addition", sd -> apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra"))));
    
        add("addition loop multi", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));;
            return op + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("addition loop div", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));;
            return op + " " + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : " " + apply(sd.get("extra")));
        });
    
        add("addition loop mod", sd -> {
            if (sd.get("extra") == null) {
                return "";
            }
            String op = symbols.find(Integer.parseInt(sd.get("op").getName()));
            return "MOD(" + apply(sd.get("expr")) + (apply(sd.get("extra")).length() == 0 ? "" : ", " + apply(sd.get("extra"))) + ")";
        });
        
        add("multiplication", sd -> {
           try {
               return ids.get(symbols.extractIdentifier(sd.get("val").getName())).getName().toUpperCase();
           }
           catch (IllegalArgumentException e) {
               try {
                   return Integer.toString(symbols.extractLiteral(sd.get("val").getName()));
               }
               catch (IllegalArgumentException e1) {
                   try {
                       if (sd.get("val").getName().contains(".")) {
                           return (sd.get("val").getName().substring(sd.get("val").getName().indexOf(".") + 1));
                       }
                   } catch (IndexOutOfBoundsException e2) {}
                   return "(" + apply(sd.get("val")) + ")";
               }
           }
        });
    }
    
    @Override
    public String generate(SymbolData data) {
        return apply(data);
    }
}
