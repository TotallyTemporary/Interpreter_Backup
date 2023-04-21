package interpreter;

import AST.Node;
import parser.ParserException;
import tokenizer.TokenizerException;

public class SemanticAnalyzer {

    private ScopedSymbolTable table;

    public SemanticAnalyzer(ScopedSymbolTable table) {
        this.table = table;
    }

    public void analyze(Node root) throws ParserException, TokenizerException, SemanticsException {
        root.analyze(table);
    }

}
