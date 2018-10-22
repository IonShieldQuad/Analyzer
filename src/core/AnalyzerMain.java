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
        
        try {
            //Create reader and writer
            reader = new BufferedReader(new FileReader(fileNameIn));
            writer = new BufferedWriter(new FileWriter(fileNameOut));
    
            System.out.println("\nLexical analysis:");
            Logger.getInstance().logln("\nLexical analysis:");
            do {
                //Reads a line
                inLine = reader.readLine();
                if (inLine != null) {
                    System.out.println(inLine);
                    Logger.getInstance().logln(inLine);
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
                        Logger.getInstance().logln(outLine);
                        writer.write(outLine);
                    }
                    catch (UnmatchedSubstringException e) {
                        System.out.println("Error: Failed to match substring: " + e.getUnmatchedSubstring());
                        Logger.getInstance().logln("Error: Failed to match substring: " + e.getUnmatchedSubstring());
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
                Logger.getInstance().logln("\nSyntax analysis:");
                System.out.println("Data size: " + data.size());
                Logger.getInstance().logln("Data size: " + data.size());
                
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
                Logger.getInstance().logln("\nSyntax analysis result:");
                System.out.println(outLine);
                Logger.getInstance().logln(outLine);
            }
            catch (PatternSearchException e) {
                System.out.println("======================================================================================");
                System.out.println("Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                System.out.println(e.getData());
                System.out.println("======================================================================================");
                Logger.getInstance().logln("======================================================================================");
                Logger.getInstance().logln("Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                Logger.getInstance().logln(e.getData());
                Logger.getInstance().logln("======================================================================================");
            }
            
            Logger.getInstance().close();
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
                        new SyntaxOperation(null, "ls"),
                        new SyntaxOperation(";", "s"),
                        new SyntaxOperation("operation", "s"),
                        new SyntaxOperation(null, "le")
                }};
                addSyntaxSymbol("operation list", patterns, null);
    
                patterns = new SyntaxOperation[][]{{
                        new SyntaxOperation(null, "ss"),
                        new SyntaxOperation("input", "s"),
                        new SyntaxOperation(null, "sb"),
                        new SyntaxOperation("output", "s"),
                        new SyntaxOperation(null, "se")
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
                        new SyntaxOperation(null , "ls"),
                        new SyntaxOperation(",", "s"),
                        new SyntaxOperation(null, "s id"),
                        new SyntaxOperation(null, "le")
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
    
    
    }
}
