package AST.functions;

import AST.Node;
import AST.NodeReturn;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;
import interpreter.SymbolType;

public class FunctionDefNode extends Node {

    /* TODO make getters and setters */

    public int id, type;
    public Symbol[] expectedArguments;
    public Node content;

    /* after analysis */
    public ScopedSymbolTable insideScope;

    /* nodes.add(new FunctionDefNode(id, type, args.toArray(new Symbol[0]), right)); */
    public FunctionDefNode(int id, int type, Symbol[] expectedArguments, Node content) {
        this.id = id;
        this.type = type;
        this.expectedArguments = expectedArguments;
        this.content = content;
    }

    /*  
        Creates declare and assign nodes for the interpreter to go through
    */
    private void analyzeArguments(ScopedSymbolTable table) throws SemanticsException {
        for (Symbol symbol : expectedArguments) {
            insideScope.define(symbol.name, symbol);
        }
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        return null;
    }

    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        /* make new scope for function inside */
        insideScope = new ScopedSymbolTable(table);

        /* define the function symbol, make it visible _outside_ the function ;) */
        Symbol funcSymbol = new Symbol(id, type, SymbolType.FUNCTION_NAME);
        table.define(id, funcSymbol);
        table.defineFunction(id, this);

        /* insert arg symbols into scope */
        analyzeArguments(table);

        content.analyze(insideScope);

        return type;
    }
}
