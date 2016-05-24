package org.universal.tokenizer;

public class NumberToken extends Token {
    private Number value;
    public NumberToken(String s) {
        value = Double.parseDouble(s);
    }

    @Override
    public Type getType() {
        return Type.NUMBER;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LiteralToken{" + value + "}";
    }
}
