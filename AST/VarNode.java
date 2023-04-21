package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopeMover;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;

public class VarNode extends Node {

    private int variableID;
    private ScopeMover scope;

    /* After semantic analysis */
    private Symbol variableSymbol;

    public VarNode(ScopeMover scope, int variableID) {
        this.variableID = variableID;
        this.scope = scope;
    }


    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        stack = scope.getCallStack(stack);

        Float value;
        value = stack.getValue(variableSymbol);

        if (value == null) {
            throw new CustomRuntimeException("Variable '" + ScopedSymbolTable.getName(variableID) + "' is null.");
        }

        return new NodeReturn(value, variableSymbol.type);
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        table = scope.getSymbolTable(table);

        variableSymbol = table.lookup(variableID);
        
        if (variableSymbol == null) {
            System.out.println(variableID);
            table.displayContents();
            throw new SemanticsException("Variable '" + ScopedSymbolTable.getName(variableID) + "' referenced before assignment");
        }
        

        return variableSymbol.type;
    }
    
}
