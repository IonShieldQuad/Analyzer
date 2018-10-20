package core;

import syntax.OperationResult;
import syntax.PatternSearchException;

public interface Parser {
    OperationResult process(String[] input) throws PatternSearchException;
}
