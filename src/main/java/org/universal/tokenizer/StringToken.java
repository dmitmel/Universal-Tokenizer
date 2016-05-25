package org.universal.tokenizer;

public class StringToken extends Token {
    private String value;
    public StringToken(String value, int indexInCode) {
        super(indexInCode);
        this.value = value;
    }
    public StringToken(String value) {
        this(value, -1);
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String getValue() {
        return value;
    }
    public String getValueWithoutQuotes() {
        return value.substring(1, value.length() - 1);
    }
}
