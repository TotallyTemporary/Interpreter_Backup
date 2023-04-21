package tokenizer;

public enum TokenType {
    INTEGER_CONSTANT("integer constant"),
    FLOAT_CONSTANT("float constant"),
    BOOLEAN_CONSTANT("boolean constant"),
    STRING_CONSTANT("string constant"),

    TYPE("type"), /* has value of id of the type of the variable this is preceding */

    RETURN("return"),

    /* arithmetic */
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
    POW("^"),

    LPAREN("("),
    RPAREN(")"),

    LBRACKET("["),
    RBRACKET("]"),

    LBRACE("{"),
    RBRACE("}"),

    EQUALS("="),
    ARROW("=>"),

    /* comparisons */
    IS_EQUAL("=="), /* equal */
    IS_GEQ(">="), /* greater than or equal to */
    IS_LEQ("<="), /* less than or equal to */
    IS_LT("<"), /* less than */
    IS_GT(">"), /* greater than */
    AND("AND"),
    OR("OR"),

    /* control flow */
    IF("if"),
    WHILE("while"),

    QUOTE("\""),

    COMMA(","),
    COLON(":"),
    SEMI(";"),
    DOT("."),

    CLASS("class"),
    NEW("new"),

    EOF("End-of-file"),

    SYMBOL("variable"); /* $x, $y, $someText, has value of id*/

    private String commonName;

    private TokenType(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public String toString() {
        return "'" + commonName + "'";
    }
}
