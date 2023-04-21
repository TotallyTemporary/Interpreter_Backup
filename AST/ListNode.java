package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class ListNode extends Node {

    private Node[] nodes;

    public ListNode(Node... nodes) {
        this.nodes = nodes;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        for (Node n : nodes) {
            n.get(stack);
        }

        return null;
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        for (Node n : nodes) {
            n.analyze(table);
        }

        return null;
    }


    
}
