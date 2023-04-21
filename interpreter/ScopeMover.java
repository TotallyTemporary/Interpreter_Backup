package interpreter;

import AST.classes.ClassDeclarationNode;
import AST.classes.ClassInstance;
import tokenizer.Token;
import tokenizer.TokenType;


public class ScopeMover {

    private Token variable;
    private ScopeMover mover;
    private boolean init = false;

    /* after analysis */
    private Symbol varSymbol;
    private ClassDeclarationNode cls;

    public ScopeMover(Token variable, ScopeMover mover) {
        this.variable = variable;
        this.mover = mover;
        init = true;
    }

    public ScopeMover() {

    }

    public CallStack getCallStack(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        if (!init) return stack;
        if (variable.type == TokenType.TYPE) return cls.staticInstance; /* accessing class */

        /* accessing instance */

        Float ind = stack.getValue(varSymbol);

        int id = (int) variable.value;
        if (ind == null) {
            throw new CustomRuntimeException("Variable " + ScopedSymbolTable.getName(id) + " has no value. ");
        }
        int index = (int) ind.floatValue();
        ClassInstance instance = stack.getInstance(index);

        if (instance == null) {
            throw new CustomRuntimeException("Variable " + ScopedSymbolTable.getName(id) + " has no instance. ");
        }

        CallStack newStack = instance.stack;
        
        if (mover == null) return newStack;
        else return mover.getCallStack(newStack);
    }

    public ScopedSymbolTable getSymbolTable(ScopedSymbolTable table) throws SemanticsException {
        if (!init) return table;

        int id = (int) variable.value;
        varSymbol = table.lookup(id);
        if (varSymbol == null) {
            throw new SemanticsException("Cannot get member of variable " + ScopedSymbolTable.getName(id) + "; is null.");
        }

        if (variable.type == TokenType.SYMBOL) cls = table.lookupClass(varSymbol.type);
        else                                   cls = table.lookupClass(id);

        if (cls == null) {
            throw new SemanticsException("Class " + ScopedSymbolTable.getName(id) + " not found.");
        }
        
        ScopedSymbolTable newTable = cls.classSymbols;

        if (mover == null) return newTable;
        else return mover.getSymbolTable(newTable);
    }
    
}
