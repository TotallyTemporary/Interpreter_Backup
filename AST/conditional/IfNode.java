package AST.conditional;

import AST.Node;
import AST.NodeReturn;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class IfNode extends Node {

    public Node contents, conditional;

    public IfNode(Node contents, Node conditional) {
        this.conditional = conditional;
        this.contents = contents;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        boolean value = ((int) conditional.get(stack).value) == 1;

        if (value) {
            contents.get(stack);
        }

        return null;
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        contents.analyze(table);
        Integer condType = conditional.analyze(table);

        if (condType != ScopedSymbolTable.BOOLEAN_TYPE) {
            throw new SemanticsException("If statements needs a boolean argument");
        }

        return null;
    }
    
}
