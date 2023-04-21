package interpreter;

import AST.Node;
import parser.ParserException;
import tokenizer.TokenizerException;

public class Interpreter {

    private Node root;

    public Interpreter(Node root) {
        this.root = root;
    }

    public float interpret() throws ParserException, TokenizerException, CustomRuntimeException {      
         
        try {
            root.get(CallStack.root);
        } catch (ReturnEvent e) {
            return e.returnValue.value;
        }

        return 0;
    }

}
