package lexis;

import core.SymbolsSystem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;

/**Lexical analyzer*/
public class StringLexer {
    public StringLexer(SymbolsSystem dictionary) {
        this.dic = dictionary;
    }

    private final SymbolsSystem dic;

    /**Processes input string into lexemes*/
    @Nullable
    @Contract("null -> null")
    public String[] processString(String inString) throws UnmatchedSubstringException {
        if (inString == null) {
            return null;
        }
        List<String> out = new ArrayList<>();
        String[] substrings;

        //Adds spaces around special symbols
        inString = addSpaces(inString);

        //Splits string into substrings by spaces
        substrings = inString.split(" ");

        for (String substring : substrings) {
            if (substring.length() == 0) {
                continue;
            }
            //Checks if the string is a reserved symbol
            if (this.isSymbol(substring)) {
                out.add(Integer.toString(dic.getSymbol(substring)));
            }
            //Checks if the string is an identifier
             else if (this.isIdentifier(substring)) {
                this.dic.addIdentifier(substring);
                out.add(dic.getIdentifierCode() + "." + dic.getIdentifier(substring));
            }
            //Checks if the string is a literal
            else if (this.isLiteral(substring)) {
                out.add(dic.getLiteralCode() + "." + substring);
            }
            //Otherwise, throws exception
            else {
                throw new UnmatchedSubstringException(substring);
            }
        }

        return out.toArray(new String[0]);
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

                outString.append(inString, i, i + matched.length());

                if (inString.length() > i + matched.length() && inString.charAt(i + matched.length()) != ' ') {
                    outString.append(" ");
                }

                i += matched.length() - 1;
            }
            else {
                outString.append(inString, i, i + 1);
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
        boolean fail = false;
        for (int i = 0; i < string.length(); ++i) {
            if (!(isDigit(string.charAt(i)) || string.charAt(i) == '.')) {
                fail = true;
                break;
            }
        }
        if (!fail) {
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
