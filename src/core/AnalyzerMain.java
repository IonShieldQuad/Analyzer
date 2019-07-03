package core;

import generator.CodeGenerator;
import generator.PascalToPL1;
import generator.SymbolData;
import lexis.PascalSymbolPack;
import lexis.SymbolPack;
import lexis.UnmatchedSubstringException;
import syntax.OperationResult;
import syntax.PascalSimpleSyntaxPack;
import syntax.PatternSearchException;
import syntax.SyntaxPack;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnalyzerMain {

    public static void main(String args[]) {
        SymbolPack symbolPack = new PascalSymbolPack();
        SyntaxPack syntaxPack = new PascalSimpleSyntaxPack();
        
        String fileNameIn = "input.txt";
        String fileNameOut = "output.txt";
        
        //Reader object, reads from file
        BufferedReader reader;
        //Writer object, writes to file
        BufferedWriter writer;
        //Input string, received from reader then processed
        String inLine;
        //Output string, created by processing input string then passed to writer
        String outLine;
        //Symbols dictionary object, contains information about symbols
        
        TDParsingSymbolsSystem td = new TDParsingSymbolsSystem(symbolPack, syntaxPack);
        Lexer lexer = td;
        Parser parser = td;
        
        
        //Add files for logging
        Logger.getInstance().addLogger("lexis", "lexisLog.txt");
        Logger.getInstance().addLogger("syntax", "syntaxLog.txt");
        Logger.getInstance().addLogger("tableGen", "tableGenLog.txt");
        Logger.getInstance().addLogger("tableResult", "tableGenRes.txt");
        Logger.getInstance().addLogger("generated", "generated.txt");
        Logger.getInstance().addLogger("syntaxResult", "syntax.txt");
    
        UnmatchedSubstringException fail = null;
        try {
            //Create reader and writer
            reader = new BufferedReader(new FileReader(fileNameIn));
            writer = new BufferedWriter(new FileWriter(fileNameOut));
            
            System.out.println("\nLexical analysis:");
            Logger.getInstance().logln("lexis", "\nLexical analysis:");
            do {
                //Reads a line
                inLine = reader.readLine();
                if (inLine != null) {
                    System.out.println(inLine);
                    Logger.getInstance().logln("lexis", inLine);
                    try {
                        //Processes line into lexemes
                        String[] out = lexer.process(inLine);
                        StringBuilder outLineBuilder = new StringBuilder();
                        for (String s : out) {
                            outLineBuilder.append(s).append(" ");
                        }
                        outLine = outLineBuilder.toString();
                        //Writes a line
                        System.out.println(outLine);
                        Logger.getInstance().logln("lexis", outLine);
                        writer.write(outLine);
                    }
                    catch (UnmatchedSubstringException e) {
                        System.out.println("Error: Failed to match substring: " + e.getUnmatchedSubstring());
                        Logger.getInstance().logln("lexis", "Error: Failed to match substring: " + e.getUnmatchedSubstring());
                        fail = e;
                    }
                    writer.newLine();
                }
            } while (inLine != null);
            //Closes reader and writer after use
            reader.close();
            writer.close();
    
            reader = new BufferedReader(new FileReader(fileNameOut));
            List<String> data = new ArrayList<>();
            do {
                //Reads a line
                inLine = reader.readLine();
                if (inLine != null) {
                    String[] s = inLine.split(" ");
                    Collections.addAll(data, s);
                }
            } while (inLine != null);
            //Closes reader and writer after use
            reader.close();
            writer.close();
    
            try {
                if (fail != null) {
                    throw fail;
                }
                
                System.out.println("\nSyntax analysis:");
                Logger.getInstance().logln("syntax", "\nSyntax analysis:");
                System.out.println("Data size: " + data.size());
                Logger.getInstance().logln("syntax", "Data size: " + data.size());
                
                OperationResult result = parser.process(data.toArray(new String[0]));
    
                if (result.isSuccess()) {
                    outLine = result.toString();
                }
                else {
                    outLine = result.getError().toString();
                    try {
                        outLine += "\nSymbol " + result.getError().getSymbol() + " is \"" + td.getSymbol(Integer.parseInt(result.getError().getSymbol())) + "\"";
                    }
                    catch (NumberFormatException ignored) {}
                }
                
                System.out.println("\nSyntax analysis result:");
                Logger.getInstance().logln("syntax", "\nSyntax analysis result:");
                System.out.println(outLine);
                Logger.getInstance().logln("syntax", outLine);
                Logger.getInstance().logln("syntaxResult", outLine);
                
                Logger.getInstance().logln("syntax", "Id table");
    
                for (Map.Entry<String, IdData> entry : lexer.getIdData().entrySet()) {
                    Logger.getInstance().logln("syntax", (entry == null ? "E == null" : entry.getKey()) + ": " + (entry == null ? "E == null" : entry.getValue() == null ? "V == null" : "OK: " + (entry.getValue().getKey() + ":" + entry.getValue().getName() + ":" + entry.getValue().getType())));
                    Logger.getInstance().logln("syntaxResult", (entry == null ? "E == null" : entry.getKey()) + ": " + (entry == null ? "E == null" : entry.getValue() == null ? "V == null" : "OK: " + (entry.getValue().getKey() + ":" + entry.getValue().getName() + ":" + entry.getValue().getType())));
                }
                //lexer.getIdData().forEach((i, d) -> Logger.getInstance().logln("syntax", i + ": " + d.getType()));
    
                //Convert output to data tree
                SymbolData sd = SymbolData.readString(outLine);
                System.out.println(sd);
                
                //Generate code
                String generated;
                CodeGenerator generator = new PascalToPL1(symbolPack, syntaxPack, lexer);
                generated = generator.generate(sd);
                System.out.println("Generated: ");
                System.out.println(generated);
                Logger.getInstance().logln("generated", generated);
                
                
            }
            catch (PatternSearchException e) {
                System.out.println("======================================================================================");
                System.out.println("Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                System.out.println(e.getData());
                System.out.println("======================================================================================");
                Logger.getInstance().logln("syntax", "======================================================================================");
                Logger.getInstance().logln("syntax", "Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                Logger.getInstance().logln("syntax", e.getData());
                Logger.getInstance().logln("syntax", "======================================================================================");
                Logger.getInstance().logln("syntaxResult", "Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                Logger.getInstance().logln("syntaxResult", e.getData());
            } catch (UnmatchedSubstringException e) {
                System.out.println("Error: Failed to match substring: " + e.getUnmatchedSubstring());
                Logger.getInstance().logln("syntax", "Error: Failed to match substring: " + e.getUnmatchedSubstring());
                Logger.getInstance().logln("syntaxResult", "Error: Failed to match substring: " + e.getUnmatchedSubstring());
            }
    
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        /*SymbolPack testSymbols = new SymbolPack() {
            @Override
            protected void initSymbols() {
                add("read");
                add("write");
                addSpaced(";");
                addSpaced(",");
                addSpaced("(");
                addSpaced(")");
                setIdentifierCode(getSymbolCount());
                setLiteralCode(getSymbolCount() + 1);
            }
        };
        
        SyntaxPack testSyntax = new SyntaxPack() {
            @Override
            protected void init() {
                setSymbolPack(testSymbols);
                
                SyntaxOperation[][] patterns;
                
                patterns = new SyntaxOperation[][]{{
                    new SyntaxOperation("operation list", "s")
                }};
                addSyntaxSymbol("main", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("operation", "s"),
                        new SyntaxOperation(";", "s"),
                        new SyntaxOperation("operation list", "s")
                }, {
                        new SyntaxOperation("operation", "s")
                        
                }};
                addSyntaxSymbol("operation list", patterns, null);
    
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("input", "s")
                }, {
                        new SyntaxOperation("output", "s")
                }};
                addSyntaxSymbol("operation", patterns, null);
    
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
                        new SyntaxOperation(",", "s"),
                        new SyntaxOperation("variable list", "s")
                }, {
                        new SyntaxOperation(null, "s id")
                }};
                addSyntaxSymbol("variable list", patterns, null);
    
                
                setMainSymbol("main");
            }
        };
        try {
            PrecedenceTable testTable = PrecedenceTable.fromPack(testSyntax);
        } catch (PatternSearchException e) {
            e.printStackTrace();
        }
    
    
        SymbolPack testSymbols0 = new SymbolPack() {
            protected void initSymbols() {
                add("program");
                add("var");
                add("integer");
                add("real");
                add("begin");
                add("end");
                add("for");
                add("to");
                add("downto");
                add("do");
                add("read");
                add("write");
                add("writeln");
                addSpaced(":=");
                addSpaced("+");
                addSpaced("-");
                addSpaced("*");
                addSpaced("/");
                addSpaced(".");
                addSpaced(";");
                addSpaced(",");
                addSpaced(":");
                addSpaced("(");
                addSpaced(")");
                setIdentifierCode(getSymbolCount());
                setLiteralCode(getSymbolCount() + 1);
            }
        };
    
        SyntaxPack testSyntax0 = new SyntaxPack() {
            @Override
            protected void init() {
                setSymbolPack(testSymbols0);
            
                SyntaxOperation[][] patterns;
            
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("header", "s"),
                        new SyntaxOperation(";", "s"),
                        new SyntaxOperation("description section", "s"),
                        new SyntaxOperation(";", "s"),
                        new SyntaxOperation("operation section", "s"),
                        new SyntaxOperation(".", "s")
                }};
                addSyntaxSymbol("main", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("program", "s"),
                        new SyntaxOperation(null, "s id")
                }};
                addSyntaxSymbol("header", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("var", "s"),
                        new SyntaxOperation("declaration", "s"),
                        new SyntaxOperation("description section loop", "s")
                }};
                addSyntaxSymbol("description section", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("declaration", "s"),
                        new SyntaxOperation("description section loop", "s")
                }, {
                
                }};
                addSyntaxSymbol("description section loop", patterns, null).setInlinePrecedence(true);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("variable list", "s"),
                        new SyntaxOperation(":", "s"),
                        new SyntaxOperation("variable type", "s"),
                        new SyntaxOperation(";", "s")
                }};
                addSyntaxSymbol("declaration", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("integer", "s")
                }, {
                        new SyntaxOperation("real", "s")
                }};
                addSyntaxSymbol("variable type", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("begin", "s"),
                        new SyntaxOperation("operation list", "s"),
                        new SyntaxOperation("end", "s")
                }};
                addSyntaxSymbol("operation section", patterns, null);
                
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("operation", "s"),
                        new SyntaxOperation("operation list loop", "s")
                }};
                addSyntaxSymbol("operation list", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation(";", "s"),
                        new SyntaxOperation("operation", "s"),
                        new SyntaxOperation("operation list loop", "s")
                }, {
                
                }};
                addSyntaxSymbol("operation list loop", patterns, null).setInlinePrecedence(true);
            
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("input", "s")
                }, {
                        new SyntaxOperation("output", "s")
                }, {
                        new SyntaxOperation("assignment", "s")
                }, {
                        new SyntaxOperation("loop", "s")
                }};
                addSyntaxSymbol("operation", patterns, null);
            
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
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop plus", "s")
                }, {
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop minus", "s")
                }};
                addSyntaxSymbol("expression", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("+", "s"),
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop plus", "s")
                }, {
                        new SyntaxOperation("+", "s"),
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop minus", "s")
                }};
                addSyntaxSymbol("expression loop plus", patterns, null).setInlinePrecedence(true);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("-", "s"),
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop plus", "s")
                }, {
                        new SyntaxOperation("-", "s"),
                        new SyntaxOperation("addition", "s"),
                        new SyntaxOperation("expression loop minus", "s")
                }};
                addSyntaxSymbol("expression loop minus", patterns, null).setInlinePrecedence(true);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("multiplication", "s"),
                        new SyntaxOperation("addition loop multi", "s")
                }, {
                        new SyntaxOperation("multiplication", "s"),
                        new SyntaxOperation("addition loop div", "s")
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
                }};
                addSyntaxSymbol("addition loop multi", patterns, null).setInlinePrecedence(true);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("/", "s"),
                        new SyntaxOperation("multiplication", "s"),
                        new SyntaxOperation("addition loop multi", "s")
                }, {
                        new SyntaxOperation("/", "s"),
                        new SyntaxOperation("multiplication", "s"),
                        new SyntaxOperation("addition loop div", "s")
                }};
                addSyntaxSymbol("addition loop div", patterns, null).setInlinePrecedence(true);
    
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
                        new SyntaxOperation(null, "s id"),
                        new SyntaxOperation("variable list loop", "s")
                }};
                addSyntaxSymbol("variable list", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation(",", "s"),
                        new SyntaxOperation(null, "s id"),
                        new SyntaxOperation("variable list loop", "s")
                }, {
                
                }};
                addSyntaxSymbol("variable list loop", patterns, null).setInlinePrecedence(true);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation("for", "s id"),
                        new SyntaxOperation("assignment", "s"),
                        new SyntaxOperation("to", "s"),
                        new SyntaxOperation("expression", "s"),
                        new SyntaxOperation("do", "s"),
                        new SyntaxOperation("operation section", "s")
                }, {
                        new SyntaxOperation("for", "s id"),
                        new SyntaxOperation("assignment", "s"),
                        new SyntaxOperation("downto", "s"),
                        new SyntaxOperation("expression", "s"),
                        new SyntaxOperation("do", "s"),
                        new SyntaxOperation("operation section", "s")
                }, {
                        new SyntaxOperation("for", "s id"),
                        new SyntaxOperation("assignment", "s"),
                        new SyntaxOperation("to", "s"),
                        new SyntaxOperation("expression", "s"),
                        new SyntaxOperation("do", "s"),
                        new SyntaxOperation("operation", "s")
                }, {
                        new SyntaxOperation("for", "s id"),
                        new SyntaxOperation("assignment", "s"),
                        new SyntaxOperation("downto", "s"),
                        new SyntaxOperation("expression", "s"),
                        new SyntaxOperation("do", "s"),
                        new SyntaxOperation("operation", "s")
                }};
                addSyntaxSymbol("loop", patterns, null);
            
                setMainSymbol("main");
            }
        };
        try {
            PrecedenceTable testTable = PrecedenceTable.fromPack(testSyntax0);
        } catch (PatternSearchException e) {
            e.printStackTrace();
        }
    
        try {
            PrecedenceTable testTable = PrecedenceTable.fromPack(syntaxPack);
        } catch (PatternSearchException e) {
            System.out.println(e.getMessage());
        }*/
        
        Logger.getInstance().close();
    }
}
