package core;

import lexis.UnmatchedSubstringException;

import java.util.List;
import java.util.Map;

public interface Lexer {
    String[] process(String input) throws UnmatchedSubstringException;
    Map<String, IdData> getIdData();
    List<IdData> getIdList();
}
