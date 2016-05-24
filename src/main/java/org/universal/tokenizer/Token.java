package org.universal.tokenizer;

public abstract class Token {
    private int indexInCode;
    public int getIndexInCode() {
        return indexInCode;
    }

    public abstract Type getType();
    public abstract Object getValue();

    public boolean isPreviousFor(Token other) {
        return this.indexInCode + 1 == other.indexInCode;
    }

    public boolean isNextFor(Token other) {
        return this.indexInCode - 1 == other.indexInCode;
    }

    enum Type {
        NUMBER, STRING, LITERAL, SINGLE_CHAR
    }
}
