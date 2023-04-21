package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class InstanceNode extends Node {

    public int value;
    private int type;

    public InstanceNode(int type) {
        this.type = type;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        if (value == 0) throw new CustomRuntimeException("Object was not initialized, cannot assign.");
        return new NodeReturn(value, type);
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return type;
    }
    
}
