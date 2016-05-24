package org.universal.tokenizer;

public class LiteralToken extends Token {
    private String value;

    public LiteralToken(String value, int indexInCode) {
        super(indexInCode);
        this.value = value;
    }
    public LiteralToken(String value) {
        this(value, -1);
    }

    @Override
    public Type getType() {
        return Type.LITERAL;
    }

    @Override
    public String getValue() {
        return value;
    }
}
