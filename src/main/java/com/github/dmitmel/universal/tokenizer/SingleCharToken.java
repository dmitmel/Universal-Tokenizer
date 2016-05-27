package com.github.dmitmel.universal.tokenizer;

public class SingleCharToken extends Token {
    private char value;
    public SingleCharToken(char value, int indexInCode) {
        super(indexInCode);
        this.value = value;
    }
    public SingleCharToken(char value) {
        this(value, -1);
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHAR;
    }

    @Override
    public Character getValue() {
        return value;
    }
}
