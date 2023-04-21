package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class ReturnNode extends Node {

    private Node right;

    public ReturnNode(Node right) {
        this.right = right;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        throw new ReturnEvent(
            right.get(stack)
        );
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        return right.analyze(table);
    }
    
}
