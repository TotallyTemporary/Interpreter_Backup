package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import AST.AssignmentNode;
import AST.DeclAndAssignNode;
import AST.DeclarationNode;
import AST.FloatNode;
import AST.IntNode;
import AST.ListNode;
import AST.Node;
import AST.ReturnNode;
import AST.StringNode;
import AST.VarNode;
import AST.arithmeticNodes.BinaryNode;
import AST.arithmeticNodes.UnaryMinus;
import AST.classes.ClassDeclarationNode;
import AST.classes.NewInstanceNode;
import AST.conditional.ComparisonNode;
import AST.conditional.IfNode;
import AST.conditional.WhileNode;
import AST.functions.FunctionCallNode;
import AST.functions.FunctionDefNode;
import interpreter.ScopeMover;
import interpreter.Symbol;
import interpreter.SymbolType;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import tokenizer.TokenizerException;

public class Parser {

    private class GetVarReturn {
        public ScopeMover mover;
        public int rootID;

        public GetVarReturn(ScopeMover mover, int rootID) {
            this.mover = mover;
            this.rootID = rootID;
        }
    }


    private Tokenizer tokenizer;

    private Token currentToken;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Node parse() throws ParserException, TokenizerException {
        goForward(); /* Get first token */

        Node result = statementList();
        expect(TokenType.EOF);

        return result;
    }

    /* scopedStatement: LBRACE statementList RBRACE SEMI */
    private Node scopedStatement() throws ParserException, TokenizerException {
        expect(TokenType.LBRACE);
        Node list = statementList();
        expect(TokenType.RBRACE);
        return list;
    }

    /* statementList: statement* */
    private Node statementList() throws ParserException, TokenizerException {
        List<Node> nodes = new ArrayList<Node>();
        while (currentToken.type == TokenType.LBRACE
                || currentToken.type == TokenType.SYMBOL
                || currentToken.type == TokenType.TYPE
                || currentToken.type == TokenType.COLON
                || currentToken.type == TokenType.RETURN
                || currentToken.type == TokenType.WHILE
                || currentToken.type == TokenType.IF
                || currentToken.type == TokenType.CLASS) {
            nodes.add(statement());
        }

        return new ListNode(nodes.toArray(new Node[0]));
    }

    /* statement: scopedStatement | declarationStatement | assignmentStatement | funcStatement */
    private Node statement() throws ParserException, TokenizerException {
        if (currentToken.type == TokenType.LBRACE) {
            Node n = scopedStatement();
            expect(TokenType.SEMI);
            return n;
        } else if (currentToken.type == TokenType.TYPE) {
            return declarationStatement();
        } else if (currentToken.type == TokenType.SYMBOL) {
            return assignmentStatement();
        } else if (currentToken.type == TokenType.COLON) {
            return funcStatement();
        } else if (currentToken.type == TokenType.RETURN) {
            return returnStatement();
        } else if (currentToken.type == TokenType.WHILE) {
            return whileStatement();
        } else if (currentToken.type == TokenType.IF) {
            return ifStatement();
        } else if (currentToken.type == TokenType.CLASS) {
            return classDeclStatement();
        } else {
            throw new ParserException("Invalid statement start. [" + currentToken.type + "].");
        }
    }


    /* CLASS SYMBOL ARROW scopedStatement */
    private Node classDeclStatement() throws ParserException, TokenizerException {
        expect(TokenType.CLASS);
        int classId = (int) currentToken.value;
        expect(TokenType.SYMBOL);
        expect(TokenType.ARROW);
        Node content = scopedStatement();
        expect(TokenType.SEMI);
        return new ClassDeclarationNode(classId, content);
    }

    private Node funcStatement() throws ParserException, TokenizerException {
        Node n = funcCall();
        expect(TokenType.SEMI);
        return n;
    }

    /* COLON (SYMBOL) callArgumentList SEMI */
    private Node funcCall() throws ParserException, TokenizerException{
        expect(TokenType.COLON);
        GetVarReturn var = getVar();
        List<Node> arguments = callArgumentList();
        FunctionCallNode callNode = new FunctionCallNode(var.mover, var.rootID, arguments.toArray(new Node[0]));

        return callNode;
    }

    /* (2+3, 4) */
    /* callArgumentList : LPAREN (expr COMMA)* expr? RPAREN */
    private List<Node> callArgumentList() throws ParserException, TokenizerException {
        expect(TokenType.LPAREN);

        /* Get argument nodes */
        List<Node> arguments = new ArrayList<Node>();
        while (currentToken.type != TokenType.RPAREN) {
            Node argument = expr();
            arguments.add(argument);
            if (currentToken.type != TokenType.COMMA) break;
            else expect(TokenType.COMMA);
        }
        expect(TokenType.RPAREN);

        return arguments;
    }

    /* ifStatement:  IF conditional ARROW scopedStatement */
    private Node ifStatement() throws ParserException, TokenizerException {
        expect(TokenType.IF);
        expect(TokenType.LPAREN);
        Node cond = conditional();
        expect(TokenType.RPAREN);
        expect(TokenType.ARROW);
        Node contents = scopedStatement();
        expect(TokenType.SEMI);
        return new IfNode(contents, cond);
    }

    private Node whileStatement() throws ParserException, TokenizerException {
        expect(TokenType.WHILE);
        expect(TokenType.LPAREN);
        Node cond = conditional();
        expect(TokenType.RPAREN);
        expect(TokenType.ARROW);
        Node contents = scopedStatement();
        expect(TokenType.SEMI);
        return new WhileNode(contents, cond);
    }

    private Node conditional() throws ParserException, TokenizerException {
        Node cond = conditionalAtom();
        
        while (currentToken.type != TokenType.RPAREN) {
            TokenType operator = currentToken.type;
            expect(TokenType.AND, TokenType.OR);
            cond = new ComparisonNode(cond, conditionalAtom(), operator);
        }

        return cond;
    }

    private Node conditionalAtom() throws ParserException, TokenizerException {
        Node left = expr();
        TokenType operator = currentToken.type;
        expect(TokenType.IS_EQUAL, TokenType.IS_GEQ, TokenType.IS_LEQ, TokenType.IS_GT, TokenType.IS_LT);
        Node right = expr();
        return new ComparisonNode(left, right, operator);
    }

    /* defineArgumentList : LPAREN (TYPE SYMBOL COMMA)* (TYPE SYMBOL SEMI)? RPAREN*/
    private List<Symbol> defineArgumentList() throws ParserException, TokenizerException {
        expect(TokenType.LPAREN);

        List<Symbol> args = new ArrayList<Symbol>();
        while (currentToken.type == TokenType.TYPE) {
            int argumentType = (int) currentToken.value;
            expect(TokenType.TYPE);
            int argumentID = (int) currentToken.value;
            expect(TokenType.SYMBOL);

            Symbol arg = new Symbol(argumentID, argumentType, SymbolType.VARDEF_TYPE);
            args.add(arg);

            if (currentToken.type != TokenType.COMMA) break;
            else expect(TokenType.COMMA);
        }
        expect(TokenType.RPAREN);
        return args;
    }

    /*
        declaration:
            Type $x;
            Type $x => 2;
            Type $x => New Type(2,3);
            Type $x(Type $y) => ...;

            Type $x, $y;
            Type $x => 2, $y => 2;
            Type $x => 2, $y;
            Type $x, $y => 3;
            Type $x => New Type(2,3), $y => New Type(3, 4);
            Type $x(Type $z) => ..., Type $y(Type $w) => ...;

        assignment:
            $x => 2;
            $x => New Type(2, 3);
            $x.$y => 2;
    */
    
    private Node declarationStatement() throws ParserException, TokenizerException {
        int type = (int) currentToken.value;
        expect(TokenType.TYPE);

        /* Do for multiple variables */
        List<Node> nodes = new ArrayList<Node>();
        while (currentToken.type == TokenType.SYMBOL) {
            int id = (int) currentToken.value;
            expect(TokenType.SYMBOL);
            
            /* Integer $x => ... */
            if (currentToken.type == TokenType.ARROW) {
                expect(TokenType.ARROW);

                /* Integer $x => New Type(2, 3) */
                if (currentToken.type == TokenType.NEW) {
                    nodes.add(new DeclAndAssignNode(id, type, newInstance()));
                } else {
                    /* Integer $x => 2 */
                    nodes.add(new DeclAndAssignNode(id, type, expr()));
                }

            /* Type $x(Type $y) => ... */
            } else if (currentToken.type == TokenType.LPAREN) {
                List<Symbol> args = defineArgumentList();
                expect(TokenType.ARROW);
                Node right = scopedStatement();
                nodes.add(new FunctionDefNode(id, type, args.toArray(new Symbol[0]), right));

            /* Type $x */
            } else {
                nodes.add(new DeclarationNode(id, type));
            }

            boolean terminate = (currentToken.type == TokenType.SEMI);
            expect(TokenType.SEMI, TokenType.COMMA);
            if (terminate) break; else continue;
        }

        return new ListNode(nodes.toArray(new Node[0]));
    }
    
    private Node assignmentStatement() throws ParserException, TokenizerException {
        List<Node> nodes = new ArrayList<Node>();
        while (currentToken.type == TokenType.SYMBOL) {
            GetVarReturn ret = getVar();
            expect(TokenType.ARROW);
            if (currentToken.type != TokenType.NEW) nodes.add(new AssignmentNode(ret.mover, ret.rootID, expr()));
            else                                    nodes.add(new AssignmentNode(ret.mover, ret.rootID, newInstance()));

            boolean terminate = (currentToken.type == TokenType.SEMI);
            expect(TokenType.SEMI, TokenType.COMMA);
            if (terminate) break; else continue;
        }

        return new ListNode(nodes.toArray(new Node[0]));
    }

    private Node newInstance() throws ParserException, TokenizerException {
        expect(TokenType.NEW);
        int classID = (int) currentToken.value;
        expect(TokenType.TYPE);

        List<Node> args = callArgumentList();

        return new NewInstanceNode(classID, args.toArray(new Node[0]));
    }

    private Node returnStatement() throws ParserException, TokenizerException {
        expect(TokenType.RETURN);
        Node retVal = expr();
        Node node = new ReturnNode(retVal);

        expect(TokenType.SEMI);

        return node;
    }

    /* expr   : term ((PLUS | MINUS) term)* */
    private Node expr() throws ParserException, TokenizerException {
        Node result = term(); /* This value will eventually contain the result of our operations. */
        
        while (
            currentToken.type == TokenType.PLUS ||
            currentToken.type == TokenType.MINUS
        ) {
            TokenType type = currentToken.type;
            expect(TokenType.PLUS, TokenType.MINUS);
            result = new BinaryNode(result, term(), type);            
        }
        return result;
    }

    /* term   : factor ((MUL | DIV) factor)* */
    private Node term() throws ParserException, TokenizerException {
        Node result = factor();

        /* operator precedence 1: type is MUL or DIV */
        while (
            currentToken.type == TokenType.MUL ||
            currentToken.type == TokenType.DIV
        ) {
            TokenType type = currentToken.type;
            expect(TokenType.MUL, TokenType.DIV);
            result = new BinaryNode(result, factor(), type);
        }
        return result;
    }

    /* factor : atom (POW factor)* */
    private Node factor() throws ParserException, TokenizerException {
        Node result = atom();

        /* operator precedence 0: square root, power */
        while (
            currentToken.type == TokenType.POW
        ) {
            TokenType type = currentToken.type;
            expect(TokenType.POW);
            result = new BinaryNode(result, factor(), type);
        }

        return result;
    }

    /* atom   : ((PLUS|MINUS) atom) | INTEGER | FLOAT | STRING |LPAREN expr RPAREN | VARIABLE | funcCall */
    private Node atom() throws ParserException, TokenizerException {
        Node result;

        if (currentToken.type == TokenType.PLUS) {
            expect(TokenType.PLUS);
            return atom();
        }

        if (currentToken.type == TokenType.MINUS) {
            expect(TokenType.MINUS);
            return new UnaryMinus(atom());
        }

        if (currentToken.type == TokenType.INTEGER_CONSTANT) {
            result = new IntNode((int) currentToken.value);
            expect(TokenType.INTEGER_CONSTANT);
            return result;
        }

        if (currentToken.type == TokenType.FLOAT_CONSTANT) {
            result = new FloatNode(currentToken.value);
            expect(TokenType.FLOAT_CONSTANT);
            return result;
        }

        if (currentToken.type == TokenType.STRING_CONSTANT) {
            result = new StringNode((int) currentToken.value);
            expect(TokenType.STRING_CONSTANT);
            return result;
        }

        if (currentToken.type == TokenType.SYMBOL) {
            GetVarReturn var = getVar();
            return new VarNode(var.mover, var.rootID);
        }

        if (currentToken.type == TokenType.COLON) {
            return funcCall();
        }

        expect(TokenType.LPAREN);
        result = expr();
        expect(TokenType.RPAREN);

        return result;
    }
    
    private GetVarReturn getVar() throws ParserException, TokenizerException {
        Token first = currentToken;
        expect(TokenType.SYMBOL, TokenType.TYPE);

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(first);
        while (currentToken.type == TokenType.DOT) {
            expect(TokenType.DOT);
            tokens.add(currentToken);
            expect(TokenType.SYMBOL, TokenType.TYPE);
        }
        /* iterate ids backwards */
        ScopeMover mover = new ScopeMover();
        
        ListIterator<Token> it = tokens.listIterator(tokens.size());
        Token root = it.previous();
        if (root.type == TokenType.TYPE) throw new ParserException("Type cannot be a variable");
        while (it.hasPrevious()) {
            Token t = it.previous();
            mover = new ScopeMover(t, mover);
        }

        return new GetVarReturn(mover, (int) root.value);
    }

    private void goForward() throws TokenizerException {
        currentToken = this.tokenizer.getNextToken();
    }

    private void expect(TokenType type) throws ParserException, TokenizerException {
        if (currentToken.type != type) throw new ParserException("Expected token type of "
                                                                    + type + " but got one of type "
                                                                    + currentToken.type);
        if (currentToken.type != TokenType.EOF) {
            goForward();
        }
    }

    private void expect(TokenType... types) throws ParserException, TokenizerException {
        for (TokenType type : types) {
            if (currentToken.type == type) {
                goForward();
                return;
            };
        }

        throw new ParserException("Expected token type of "
                                    + Arrays.toString(types) + " but got one of type "
                                    + currentToken.type);
    }

}
