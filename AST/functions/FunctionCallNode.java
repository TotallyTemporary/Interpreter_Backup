package AST.functions;

import AST.Node;
import AST.NodeReturn;
import AST.NodeUtil;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopeMover;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;

public class FunctionCallNode extends Node {

    private int id;
    private ScopeMover mover;

    private Node[] arguments;

    /* After semantic analysis */
    private FunctionDefNode defNode;

    /* scope is where the function resides 
       funcID is the id of the function, you can look up the FunctionDefNode with this 
       arguments is a bunch of Nodes that resolve to values {2, 3, 4} in :$x(2, 3, 4); */
    public FunctionCallNode(ScopeMover mover, int funcID, Node[] arguments) {
        this.id = funcID;
        this.mover = mover;
        this.arguments = arguments;
    }

    private void execArgs(CallStack insideStack, CallStack outsideStack) throws CustomRuntimeException, ReturnEvent {
        for (int i = 0; i < arguments.length; i++) {
            NodeReturn ret = arguments[i].get(outsideStack);
            if (ret == null) {
                throw new CustomRuntimeException("Function '" + ScopedSymbolTable.getName(id) + "' parameter no. " + (i+1)
                                                + " has no value.");
            }

            insideStack.setValue(defNode.expectedArguments[i], ret.value);
        }
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        CallStack funcStack = mover.getCallStack(stack); /* where the function is */
        CallStack newStack = new CallStack(funcStack); /* inside the function */

        /* Make parameters */
        execArgs(newStack, stack);
        
        /* Run function contents */
        NodeReturn returnValue = null;
        try {
            defNode.content.get(newStack);
        } catch (ReturnEvent e) {
            returnValue = e.returnValue;
        }

        if (returnValue == null) {
            String funcName = ScopedSymbolTable.getName(id);
            throw new CustomRuntimeException("Function '" + funcName + "' has return type but returned nothing. ");
        }

        return returnValue;
    }

    public Integer analyze(ScopedSymbolTable callFromTable) throws SemanticsException {
        ScopedSymbolTable funcContainerTable = mover.getSymbolTable(callFromTable); // the class that contains func

        /* Find function definition */
        defNode = funcContainerTable.lookupFunc(id);
        if (defNode == null) {
            throw new SemanticsException("Function '" + ScopedSymbolTable.getName(id) + "' is not defined.");
        }

        /* analyze arguments */

        /* check argument list length */
        if (defNode.expectedArguments.length != arguments.length) {
            throw new SemanticsException("Function '" + ScopedSymbolTable.getName(id) + "' expects "
                                        + defNode.expectedArguments.length + " arguments but was passed "
                                        + arguments.length);
        }

        /* check arguments type */
        for (int i = 0; i < arguments.length; i++) {
            Node argNode = arguments[i];
            Symbol s = defNode.expectedArguments[i];

            Integer passedType = argNode.analyze(callFromTable);
            Integer expectedType = s.type;

            if (expectedType != NodeUtil.convert(passedType, expectedType)) {
                throw new SemanticsException("Function '" + ScopedSymbolTable.getName(id) + "' argument " + (i+1)
                                            + " expects type " + ScopedSymbolTable.getName(expectedType) + " but got "
                                            + ScopedSymbolTable.getName(passedType) + " instead. ");
            }
        }

        return defNode.type;
    }
}
