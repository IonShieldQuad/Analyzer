package core;

import lexis.UnmatchedSubstringException;

public interface Lexer {
    String[] process(String input) throws UnmatchedSubstringException;
}
