package core;

import lexis.PascalSymbolPack;
import lexis.SymbolPack;
import lexis.UnmatchedSubstringException;
import syntax.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalyzerMain {

    public static void main(String args[]) {

        SymbolPack symbolPack = new PascalSymbolPack();
        SyntaxPack syntaxPack = new PascalSyntaxPack();
        
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
        
        Logger.getInstance().addLogger("lexis", "lexisLog.txt");
        Logger.getInstance().addLogger("syntax", "syntaxLog.txt");
        Logger.getInstance().addLogger("tableGen", "tableGenLog.txt");
        
        
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
                System.out.println("\nSyntax analysis:");
                Logger.getInstance().logln("syntax", "\nSyntax analysis:");
                System.out.println("Data size: " + data.size());
                Logger.getInstance().logln("syntax", "Data size: " + data.size());
                
                OperationResult result = parser.process(data.toArray(new String[0]));
    
                if (result.isSuccess()) {
                    outLine = result.toString(" ");
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
            }
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        SymbolPack testSymbols = new SymbolPack() {
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
                addTerminalsFromPack(testSymbols);
                
                SyntaxOperation[][] patterns;
                
                patterns = new SyntaxOperation[][]{{
                    new SyntaxOperation("operation list", "s")
                }};
                addSyntaxSymbol("main", patterns, null);
    
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
                addSyntaxSymbol("operation list loop", patterns, null);
    
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
                        new SyntaxOperation("variable list loop", "s")
                }};
                addSyntaxSymbol("variable list", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation(",", "s"),
                        new SyntaxOperation(null, "s id"),
                        new SyntaxOperation("variable list loop", "s")
                }, {
                
                }};
                addSyntaxSymbol("variable list loop", patterns, null);
                
                setMainSymbol("main");
            }
        };
        try {
            PrecedenceTable testTable = PrecedenceTable.fromPack(testSyntax);
        } catch (PatternSearchException e) {
            e.printStackTrace();
        }
        
        Logger.getInstance().close();
    }
}
