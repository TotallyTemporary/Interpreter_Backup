package AST.classes;

import AST.Node;
import AST.NodeReturn;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import interpreter.Symbol;
import interpreter.SymbolType;

public class ClassDeclarationNode extends Node {

    private int name;
    public Node content;

    /* after analysis */
    public ScopedSymbolTable classSymbols;
    public CallStack staticInstance;

    public ClassDeclarationNode(int name, Node content) {
        this.name = name;
        this.content = content;
    }

    private CallStack makeStaticInstance(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        CallStack staticStack = new CallStack(stack);

        content.get(staticStack);

        return staticStack;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        System.out.println("making static instance for " + ScopedSymbolTable.getName(name));
        staticInstance = makeStaticInstance(stack);
        return null;
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        if (table.shortLookup(name) != null) {
            throw new SemanticsException("Can't define variable " +
                                ScopedSymbolTable.getName(name) +
                                        "; was already defined.");
        }

        /* define the class symbols to be called outside the class scope */
        Symbol classSymbol = new Symbol(name, ScopedSymbolTable.INTEGER_TYPE, SymbolType.CLASS_NAME);
        table.define(name, classSymbol);
        table.defineClass(name, this);

        classSymbols = new ScopedSymbolTable(table);
        content.analyze(classSymbols);

        return null;
    }
}
