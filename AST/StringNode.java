package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class StringNode extends Node {

    private int id;

    public StringNode(int id) {
        this.id = id;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return new NodeReturn(id, ScopedSymbolTable.STRING_TYPE);
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return ScopedSymbolTable.STRING_TYPE;
    }
    
}
