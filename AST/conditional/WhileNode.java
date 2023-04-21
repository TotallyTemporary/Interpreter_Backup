package AST.conditional;

import AST.Node;
import AST.NodeReturn;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class WhileNode extends Node {

    public Node contents, conditional;

    public WhileNode(Node contents, Node conditional) {
        this.conditional = conditional;
        this.contents = contents;
    }

    private boolean getConditional(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return ((int) conditional.get(stack).value) == 1;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        while (getConditional(stack)) {
            contents.get(stack);
        }

        return null;
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        contents.analyze(table);
        Integer condType = conditional.analyze(table);

        if (condType != ScopedSymbolTable.BOOLEAN_TYPE) {
            throw new SemanticsException("While statements needs a boolean argument");
        }

        return null;
    }
    
}
