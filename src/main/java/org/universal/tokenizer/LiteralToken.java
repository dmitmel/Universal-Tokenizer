package org.universal.tokenizer;

public class LiteralToken extends Token {
    private String value;
    public LiteralToken(String value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.LITERAL;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LiteralToken{" + value + "}";
    }
}
