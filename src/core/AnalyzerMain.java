package core;

import lexis.PascalSymbolPack;
import lexis.StringLexer;
import lexis.SymbolPack;
import lexis.UnmatchedSubstringException;
import syntax.PascalSyntaxPack;
import syntax.PatternSearchException;
import syntax.SyntaxPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
        SymbolsDict dic = new SymbolsDict(symbolPack, syntaxPack);
        StringLexer lex = new StringLexer(dic);
        
        try {
            //Create reader and writer
            reader = new BufferedReader(new FileReader(fileNameIn));
            writer = new BufferedWriter(new FileWriter(fileNameOut));
    
            System.out.println("\nLexical analysis:");
            do {
                //Reads a line
                inLine = reader.readLine();
                if (inLine != null) {
                    System.out.println(inLine);
                    try {
                        //Processes line into lexemes
                        outLine = lex.processString(inLine);
                        //Writes a line
                        System.out.println(outLine);
                        writer.write(outLine);
                    }
                    catch (UnmatchedSubstringException e) {
                        System.out.println("Error: Failed to match substring: " + e.getUnmatchedSubstring());
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
                System.out.println("Data size: " + data.size());
                
                outLine = dic.analyzeSyntax(data.toArray(new String[]{}));
    
                System.out.println("\nSyntax analysis result:");
                System.out.println(outLine);
            }
            catch (PatternSearchException e) {
                System.out.println("======================================================================================");
                System.out.println("Error in syntax pattern search in symbol '" + e.getName() + "', position: " + e.getIndex());
                System.out.println(e.getData());
                System.out.println("======================================================================================");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
