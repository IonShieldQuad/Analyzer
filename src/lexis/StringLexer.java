package lexis;

import core.SymbolsDict;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Character.isDigit;

public class StringLexer {
    public StringLexer(SymbolsDict dictionary) {
        this.dic = dictionary;
    }

    private final SymbolsDict dic;

    /**Processes input string into lexemes*/
    @Nullable
    @Contract("null -> null")
    public String processString(String inString) throws UnmatchedSubstringException {
        if (inString == null) {
            return null;
        }
        String outString = "";
        String[] substrings;

        //Adds spaces around special symbols
        inString = this.addSpaces(inString);

        //Splits string into substrings by spaces
        substrings = inString.split(" ");

        for (String substring : substrings) {
            if (substring.length() == 0) {
                continue;
            }
            //Checks if the string is a reserved symbol
            if (this.isSymbol(substring)) {
                outString += this.dic.getSymbol(substring) + " ";
            }
            //Checks if the string is an identifier
             else if (this.isIdentifier(substring)) {
                this.dic.addIdentifier(substring);
                outString += this.dic.getIdentifierCode() + "." + this.dic.getIdentifier(substring) + " ";
            }
            //Checks if the string is a literal
            else if (this.isLiteral(substring)) {
                outString += this.dic.getLiteralCode() + "." + substring + " ";
            }
            //Otherwise, throws exception
            else {
                throw new UnmatchedSubstringException(substring);
            }
        }

        return outString;
    }

    public SymbolsDict getDictionary() {
        return this.dic;
    }

    /**Adds spaces around reserved symbols*/
    @NotNull
    private String addSpaces(@NotNull String inString) {
        StringBuilder outString = new StringBuilder();

        for (int i = 0; i < inString.length(); ++i) {
            String matched = null;
            for (String symbol : this.dic.spacedSymbolSet()) {
                if (inString.startsWith(symbol, i)) {
                    if (matched == null || matched.length() < symbol.length()) {
                        matched = symbol;
                    }
                }
            }
            if (matched != null) {
                if (i > 0 && inString.charAt(i - 1) != ' ') {
                    outString.append(" ");
                }

                outString.append(inString.substring(i, i + matched.length()));

                if (inString.length() > i + matched.length() && inString.charAt(i + matched.length()) != ' ') {
                    outString.append(" ");
                }

                i += matched.length() - 1;
            }
            else {
                outString.append(inString.substring(i, i + 1));
            }
        }

        return outString.toString();
    }

    private boolean isSymbol(@NotNull String string) {
        for (String symbol : this.dic.symbolSet()) {
            if (string.equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIdentifier(@NotNull String string) {
        if (string.length() == 0) {
            return false;
        }
        boolean matched = true;
        if (string.equals("true") || string.equals("false")) {
            matched = false;
        }
        if (isDigit(string.charAt(0))) {
            matched = false;
        }
        if (string.charAt(0) == '\"' || string.charAt(string.length() - 1) == '\"') {
            matched = false;
        }
        if (string.charAt(0) == '\'' || string.charAt(string.length() - 1) == '\'') {
            matched = false;
        }
        return matched;
    }

    private boolean isLiteral(@NotNull String string) {
        if (string.length() == 0) {
            return false;
        }
        boolean matched = false;
        if (string.equals("true") || string.equals("false")) {
            matched = true;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (!(isDigit(string.charAt(i)) || string.charAt(i) == '.')) {
                break;
            }
            matched = true;
        }
        if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"') {
            matched = true;
        }
        if (string.charAt(0) == '\'' && string.charAt(string.length() - 1) == '\'') {
            matched = true;
        }
        return matched;
    }
}
