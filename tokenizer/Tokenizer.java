package tokenizer;

import java.util.HashMap;

import interpreter.ScopedSymbolTable;
import interpreter.Symbol;

public class Tokenizer {

    private String source;

    private int position = 0;

    private char currentChar = 'A';

    /* Bit of a hack, a tokenizer shouldn't need this, but we're converting
        types from strings into IDs in here, so we need the global built-in definitions */
    private ScopedSymbolTable globalScope;

    /* Use this map if you need keywords consiting of alphabet characters (DIV, FUN, PUBLIC) */
    private static HashMap<String, TokenType> keywords = new HashMap<String, TokenType>();
    static {
        keywords.put("Return", TokenType.RETURN);
        keywords.put("If", TokenType.IF);
        keywords.put("While", TokenType.WHILE);
        keywords.put("And", TokenType.AND);
        keywords.put("Or", TokenType.OR);
        keywords.put("Class", TokenType.CLASS);
        keywords.put("New", TokenType.NEW);
    }

    /* Use this map for boring one-character, one-tokentype without value things */
    private static HashMap<Character, TokenType> tokenMap = new HashMap<Character, TokenType>();
    static {
        tokenMap.put('+', TokenType.PLUS);
        tokenMap.put('-', TokenType.MINUS);
        tokenMap.put('*', TokenType.MUL);
        tokenMap.put('/', TokenType.DIV);
        tokenMap.put('^', TokenType.POW);
        tokenMap.put(';', TokenType.SEMI);
        tokenMap.put(':', TokenType.COLON);
        tokenMap.put(',', TokenType.COMMA);
        tokenMap.put('(', TokenType.LPAREN);
        tokenMap.put(')', TokenType.RPAREN);
        tokenMap.put('{', TokenType.LBRACE);
        tokenMap.put('}', TokenType.RBRACE);
        tokenMap.put('.', TokenType.DOT);
    }

    public Tokenizer(String source, ScopedSymbolTable globalScope) {
        this.source = source;
        this.currentChar = source.charAt(0);
        this.globalScope = globalScope;
    } 
    
    public Token getNextToken() throws TokenizerException {
        Token t = getNextToken1();
        // System.out.println(t);
        return t;
    }

    public Token getNextToken1() throws TokenizerException {
        if (position >= source.length()) {
            return new Token(TokenType.EOF, 0);
        }

        while ((currentChar == ' ' ||
                currentChar == '\n') &&
                position < source.length()) {
            goForward();
            return getNextToken1();
        }

        if (position >= source.length()) {
            return new Token(TokenType.EOF, 0);
        }

        if (currentChar == '/' && peekNextChar() == '/') {
            skipComment();
            return getNextToken1();
        }

        if (currentChar == '$') {
            /* ID */
            goForward();
            return getVariable();
        }

        if (currentChar == '"') {
            goForward();
            return getStringConstant();
        }

        /* One-character simple tokens */
        if (tokenMap.containsKey(currentChar)) {
            TokenType type = tokenMap.get(currentChar);
            goForward();

            return new Token(type, 0);
        }

        if (currentChar == '=') {
            goForward();
            if (currentChar == '>') {
                goForward();
                return new Token(TokenType.ARROW, 0);
            } else if (currentChar == '=') {
                goForward();
                return new Token(TokenType.IS_EQUAL, 0);
            } else {
                goForward();
                return new Token(TokenType.EQUALS, 0);
            }
        }

        /* comparisons */
        if (currentChar == '>') {
            goForward();
            if (currentChar == '=') {
                return new Token(TokenType.IS_GEQ, 0);
            } else {
                return new Token(TokenType.IS_GT, 0);
            }
        }

        if (currentChar == '<') {
            goForward();
            if (currentChar == '=') {
                return new Token(TokenType.IS_LEQ, 0);
            } else {
                return new Token(TokenType.IS_LT, 0);
            }
        }

        if (Character.isAlphabetic(currentChar) && currentChar != ' ') {
            /* Check keywords */
            return getKeyword();
        }

        if (Character.isDigit(currentChar)) {
            return getNumeric();
        }

        throw new TokenizerException("Invalid token: [" + currentChar + "]");
    }

    private Token getStringConstant() throws TokenizerException {
        String name = "";
        boolean advanced = true;
        while (currentChar != '"' && advanced) {
            name += currentChar;
            advanced = goForward();
        }
        goForward();
        int id = ScopedSymbolTable.getStringID(name);
        return new Token(TokenType.STRING_CONSTANT, id);
    }

    private Token getKeyword() throws TokenizerException {
        String name = "";
        while (Character.isAlphabetic(currentChar)) {
            name += currentChar;
            goForward();
        }

        Symbol typeSymbol = globalScope.lookup(name);
        TokenType keywordType = keywords.get(name);

        if (typeSymbol != null && keywordType != null) {
            throw new TokenizerException("Keyword AND type exist for " + name);
        }

        if (typeSymbol != null) {
            return new Token(TokenType.TYPE, typeSymbol.name);
        }

        if (keywordType != null) {
            return new Token(keywordType, 0);
        }
        
        /* this is a user defined type */
        int id = getTypeValue(name);
        return new Token(TokenType.TYPE, id);
    }

    /* Note advanced flag */
    /* If there is a number at the end of the file, the goForward() func won't go forward, */
    /* but it will return false. */
    private Token getNumeric() {
        String stringRepr = "";
        boolean advanced = true;
        while ((Character.isDigit(currentChar) || currentChar == '.') && advanced) {
            stringRepr += currentChar;
            advanced = goForward();
        }

        if (stringRepr.contains(".")) {
            return new Token(TokenType.FLOAT_CONSTANT, Float.parseFloat(stringRepr));
        }

        return new Token(TokenType.INTEGER_CONSTANT, Integer.parseInt(stringRepr));
    }

    private Token getVariable() {
        String symbol = "";
        while (Character.isAlphabetic(currentChar)) {
            symbol += currentChar;
            goForward();
        }
        int val = getSymbolValue(symbol);
        return new Token(TokenType.SYMBOL, val);
    }

    private int getSymbolValue(String name) {
        int value = ScopedSymbolTable.getID(name);
        
        return value;
    } 

    private int getTypeValue(String name) {
        int value = ScopedSymbolTable.getID(name);

        return value;
    }

    private void skipComment() {
        boolean advanced = true;
        while (currentChar != '\n' && advanced) {
            advanced = goForward();
        }
    }

    private boolean goForward() {
        this.position += 1;

        if (position >= source.length()) {
            return false;
        }

        this.currentChar = getCurrentCharacter();
        return true;
    }

    private char peekNextChar() {
        int peeking = position+1;

        if (peeking >= (source.length() - 1)) {
            return ' ';
        }

        return this.source.charAt(peeking);
    }

    private char getCurrentCharacter() {
        char c = this.source.charAt(position);
        return c;
    } 

}
