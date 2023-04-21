package AST;

import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopeMover;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;
import interpreter.SymbolType;

public class AssignmentNode extends Node {

    private int variableID;
    private ScopeMover mover;
    private Node right;

    /* After semantic analysis */
    private Symbol variableSymbol;

    public AssignmentNode(ScopeMover mover, int variableID, Node right) {
        this.mover = mover;
        this.variableID = variableID;
        this.right = right;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        /* Get the value we're assigning */
        NodeReturn val = right.get(stack);
        CallStack newStack = mover.getCallStack(stack);

        /* automatic conversions */
        NodeUtil.convert(val, variableSymbol.type);

        newStack.setValue(variableSymbol, val.value);                                                      
        return val;
    }

    @Override
    public Integer analyze(ScopedSymbolTable scope) throws SemanticsException {
        Integer rightType = right.analyze(scope);

        ScopedSymbolTable newScope = mover.getSymbolTable(scope);

        variableSymbol = newScope.lookup(variableID);
        if (variableSymbol == null) {
            throw new SemanticsException("Tried to assign to variable that wasn't defined");
        }

        if (variableSymbol.category == SymbolType.BUILT_IN_VAR) {
            String name = ScopedSymbolTable.getName(variableID);
            throw new SemanticsException("Tried to assign to final variable '" + name + "'.");
        }

        rightType = NodeUtil.convert(rightType, variableSymbol.type);
        if (rightType != variableSymbol.type) {
            String name = ScopedSymbolTable.getName(variableID);
            throw new CustomRuntimeException("Variable '" + name + "' expected type " +
                                        ScopedSymbolTable.getName(variableSymbol.type) + " but got " +
                                        ScopedSymbolTable.getName(rightType) + " instead.");
        }
        
        return variableSymbol.type;
    }
    
}
