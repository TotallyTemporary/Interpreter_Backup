package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public abstract class Node {

    /* Call this to "run" the node */
    /* Returns NodeReturn object with value and type */
    public abstract NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent;

    /* Returns Integer type */
    public abstract Integer analyze(ScopedSymbolTable table) throws SemanticsException;
}
