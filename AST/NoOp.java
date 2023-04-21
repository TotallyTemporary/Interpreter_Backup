package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class NoOp extends Node {

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException {
        return null;
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return null;
    }
}
