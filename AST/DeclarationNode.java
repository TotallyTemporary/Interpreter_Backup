package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;
import interpreter.SymbolType;

public class DeclarationNode extends Node {

    private int variableID;
    private int variableType;

    public DeclarationNode(int variableID, int variableType) {
        this.variableID = variableID;
        this.variableType = variableType;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return null;
    }

    @Override
    public Integer analyze(ScopedSymbolTable scope) throws SemanticsException {
        if (scope.shortLookup(variableID) != null) {
            throw new SemanticsException("Can't define variable " +
                                ScopedSymbolTable.getName(variableID) +
                                        "; was already defined.");
        }

        scope.define(variableID, new Symbol(variableID, variableType, SymbolType.VARDEF_TYPE));
        return variableType;
    }
    
}
