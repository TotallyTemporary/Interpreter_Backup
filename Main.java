import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import AST.Node;
import interpreter.CustomRuntimeException;
import interpreter.IO;
import interpreter.Interpreter;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticAnalyzer;
import interpreter.SemanticsException;
import parser.Parser;
import parser.ParserException;
import tokenizer.Tokenizer;
import tokenizer.TokenizerException;

public class Main {
    public static void main(String[] args) throws TokenizerException, ParserException, SemanticsException, CustomRuntimeException, ReturnEvent {

        String source = """
            :System.$WriteFloat(2);
        """;

        /* if a source file was passed */
        if (args.length > 0) {
            String path = args[0]; 
            try {
                source = Files.readString(Path.of(path));
                source = source.replace("\r", "");
            } catch (IOException e) {
                System.err.println("Couldn't read source file :(");
                e.printStackTrace();
            }
        }

        System.out.println("-----------------running!-----------------");

        final boolean PRINT_TIMINGS = false;

        long start = System.nanoTime();
        ScopedSymbolTable globalScope = new ScopedSymbolTable();
        /* Tokenize and parse */
        Tokenizer tokenizer = new Tokenizer(source, globalScope);
        Parser parser = new Parser(tokenizer);
        Node root = parser.parse();
        long end = System.nanoTime();
        if (PRINT_TIMINGS) System.out.println("tokenize & parse : " + (end - start) / 1_000_000 + " ms");
        
        start = System.nanoTime();
        /* Do semantic analysis */
        SemanticAnalyzer analyzer = new SemanticAnalyzer(globalScope);
        analyzer.analyze(root);
        end = System.nanoTime();
        if (PRINT_TIMINGS) System.out.println("semantic analysis : " + (end - start) / 1_000_000 + " ms");

        /* Run code */
        start = System.nanoTime();
        Interpreter interpreter = new Interpreter(root);
        float result = interpreter.interpret();
        end = System.nanoTime();
        if (PRINT_TIMINGS) System.out.println("interpret : " + (end - start) / 1_000_000 + " ms");

        System.out.println("exit code: " + result);

        IO.close();
    }
}
