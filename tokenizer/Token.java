package tokenizer;

public class Token {
    
    public TokenType type;
    public float value;

    public Token(TokenType type, float value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return ("[" + type + "]: " + value);
    }

}
