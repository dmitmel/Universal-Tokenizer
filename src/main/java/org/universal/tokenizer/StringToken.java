package org.universal.tokenizer;

public class StringToken extends Token {
    private String value;
    public StringToken(String value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.STRING;
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
