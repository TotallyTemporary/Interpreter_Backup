package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class FloatNode extends Node {

    private float value;

    public FloatNode(float value) {
        this.value = value;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return new NodeReturn(value, ScopedSymbolTable.FLOAT_TYPE);
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return ScopedSymbolTable.FLOAT_TYPE;
    }
    
}
