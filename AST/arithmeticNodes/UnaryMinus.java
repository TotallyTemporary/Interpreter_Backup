package AST.arithmeticNodes;

import AST.Node;
import AST.NodeReturn;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class UnaryMinus extends Node {

    private Node node;

    public UnaryMinus(Node node) {
        this.node = node;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        NodeReturn ret = node.get(stack);
        ret.value = -ret.value;
        return ret;
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        node.analyze(table);
        return null;
    }
    
}
