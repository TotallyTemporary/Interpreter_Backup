package AST.classes;

import AST.Node;
import AST.NodeReturn;
import AST.NodeUtil;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;

public class InstanceAssignmentNode extends Node {

    private int root, var;
    private Node right;

    /* after analysis */
    private Symbol rootSymbol, varSymbol;

    public InstanceAssignmentNode(int root, int var, Node right) {
        this.root = root;
        this.var = var;
        this.right = right;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        NodeReturn val = right.get(stack);
        NodeUtil.convert(val, varSymbol.type);

        int index = (int) stack.getValue(rootSymbol).floatValue(); /* TODO Add checks */
        CallStack newStack = stack.getInstance(index).stack;

        /* Don't allow assigning to type that is not the variable */
        if (val.type == varSymbol.type) {
            newStack.setValue(varSymbol, val.value);                                                      
            return val;
        }

        String name = ScopedSymbolTable.getName(var);
        throw new CustomRuntimeException("Variable '" + name + "' expected type " +
                                        ScopedSymbolTable.getName(varSymbol.type) + " but got " +
                                        ScopedSymbolTable.getName(val.type) + " instead.");

    }

    @Override
    public Integer analyze(ScopedSymbolTable scope) throws SemanticsException {
        right.analyze(scope);
        rootSymbol = scope.lookup(root);
        
        if (rootSymbol == null) {
            throw new SemanticsException("Instance '" + ScopedSymbolTable.getName(root) + "' referenced before assignment");
        }

        /* TODO Add further checks */

        ClassDeclarationNode type = scope.lookupClass(rootSymbol.type);
        varSymbol = type.classSymbols.lookup(var);
        
        return varSymbol.type;
    }
    
}
