package core;

import lexis.PascalSymbolPack;
import lexis.StringLexer;
import lexis.SymbolPack;
import lexis.UnmatchedSubstringException;
import syntax.OperationResult;
import syntax.PascalSyntaxPack;
import syntax.PatternSearchException;
import syntax.SyntaxPack;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatException;
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
        SymbolsDict dic = new SymbolsDict(symbolPack, syntaxPack);
        StringLexer lex = new StringLexer(dic);
        
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
                        outLine = lex.processString(inLine);
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
                
                OperationResult result = dic.analyzeSyntax(data.toArray(new String[]{}));
    
                if (result.isSuccess()) {
                    outLine = result.toString(" ");
                }
                else {
                    outLine = result.getError().toString();
                    try {
                        outLine += "\nSymbol " + result.getError().getSymbol() + " is \"" + dic.getSymbol(Integer.parseInt(result.getError().getSymbol())) + "\"";
                    }
                    catch (IllegalFormatException ignored) {}
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
    }
}
