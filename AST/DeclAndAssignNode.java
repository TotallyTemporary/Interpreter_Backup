package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;
import interpreter.SymbolType;

public class DeclAndAssignNode extends Node {

    private int variableID;
    private int variableType;
    private Node right;

    /* After semantic analysis */
    private Symbol variableSymbol;

    public DeclAndAssignNode(int variableID, int variableType, Node right) {
        this.variableID = variableID;
        this.variableType = variableType;
        this.right = right;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        NodeReturn val = right.get(stack);
        NodeUtil.convert(val, variableSymbol.type);

        if (val.type == variableSymbol.type) {
            stack.setValue(variableSymbol, val.value);
            return val;
        }

        String name = ScopedSymbolTable.getName(variableID);
        throw new CustomRuntimeException("Variable '" + name + "' expected type " +
                                        ScopedSymbolTable.getName(variableSymbol.type) + " but got " +
                                        ScopedSymbolTable.getName(val.type) + " instead.");
    }

    @Override
    public Integer analyze(ScopedSymbolTable scope) throws SemanticsException {
        right.analyze(scope);

        if (scope.shortLookup(variableID) != null) {
            throw new SemanticsException("Can't define variable " +
                                ScopedSymbolTable.getName(variableID) +
                                        "; was already defined.");
        }

        scope.define(variableID, this.variableSymbol = new Symbol(variableID, variableType, SymbolType.VARDEF_TYPE));
        return variableType;
    }
    
}
