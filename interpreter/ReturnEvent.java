package interpreter;

import AST.NodeReturn;

/* When we want to return from a function, a node throws a ReturnEvent exception. */
/* This will be thrown up through the "chain of command" until we catch it at the function call node or */
/* in the global scope. */
public class ReturnEvent extends Exception {

    public NodeReturn returnValue;

    public ReturnEvent(NodeReturn returnValue) {
        super();
        this.returnValue = returnValue;
    }

}
