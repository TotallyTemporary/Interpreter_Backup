package AST.classes;

import AST.InstanceNode;
import AST.Node;
import AST.NodeReturn;
import AST.functions.FunctionCallNode;
import AST.functions.FunctionDefNode;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopeMover;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;

public class NewInstanceNode extends Node {

    private int className;
    private Node[] arguments;

    /* after analysis */
    private InstanceNode selfArg;
    private FunctionCallNode constructCall;
    private ClassDeclarationNode node;

    public NewInstanceNode(int className, Node[] arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        CallStack instanceStack = new CallStack(stack);
        
        /* make the instance and finalize the constructor */
        ClassInstance instance = new ClassInstance(node, instanceStack);
        int id = stack.setInstance(instance);
        selfArg.value = id;

        /* Initialize class contents (variables etc.) */
        node.content.get(instanceStack);

        /* call the new and improved constructor and return the new object */
        return constructCall.get(instanceStack);
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        node = table.lookupClass(className);

        if (node == null) {
            throw new SemanticsException("No class exists named " + ScopedSymbolTable.getName(className));
        }

        ScopedSymbolTable classScope = node.classSymbols;

        /* Make constructor function */
        selfArg = new InstanceNode(className);
        Node[] newArgs = new Node[arguments.length + 1];
        newArgs[0] = selfArg;
        for (int i = 0; i < arguments.length; i++) {
            newArgs[i+1] = arguments[i];
        }

        int funcID = ScopedSymbolTable.getID("Construct");

        /* make sure Construct function exists */
        FunctionDefNode existingConstruct = classScope.lookupFunc(funcID);
        if (existingConstruct == null) {
            throw new SemanticsException("Cannot make instance of class " + ScopedSymbolTable.getName(className)
                                        + ", doesn't have $Construct method.");
        }

        /* Make new construct function and analyze it, this will be called on new object creation */
        constructCall = new FunctionCallNode(new ScopeMover(), funcID, newArgs);
        constructCall.analyze(classScope);

        return className;
    }
    
}
