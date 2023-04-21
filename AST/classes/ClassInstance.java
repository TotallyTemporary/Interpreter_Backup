package AST.classes;

import interpreter.CallStack;

public class ClassInstance {

    public ClassDeclarationNode type;
    public CallStack stack;

    public ClassInstance(ClassDeclarationNode type, CallStack stack) {
        this.type = type;
        this.stack = stack;
    }

}
