package org.universal.tokenizer;

public class NumberToken extends Token {
    private Number value;
    public NumberToken(String s, int indexInCode) {
        super(indexInCode);
        // TODO: make deciding number type
        value = Double.parseDouble(s);
    }
    public NumberToken(String s) {
        this(s, -1);
    }

    @Override
    public Type getType() {
        return Type.NUMBER;
    }

    @Override
    public Number getValue() {
        return value;
    }
}