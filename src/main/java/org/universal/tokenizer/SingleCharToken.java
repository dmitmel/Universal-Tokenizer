package org.universal.tokenizer;

public class SingleCharToken extends Token {
    private char value;
    public SingleCharToken(char value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHAR;
    }

    @Override
    public Character getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "SingleCharToken{" + value + "}";
    }
}
