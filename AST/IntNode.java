package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class IntNode extends Node {

    private int value;

    public IntNode(int value) {
        this.value = value;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return new NodeReturn(value, ScopedSymbolTable.INTEGER_TYPE);
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return ScopedSymbolTable.INTEGER_TYPE;
    }
    
}
