package com.github.dmitmel;

public abstract class Token {
    private int indexInCode;
    public int getIndexInCode() {
        return indexInCode;
    }

    public Token(int indexInCode) {
        this.indexInCode = indexInCode;
    }

    public abstract Object getValue();

    public boolean isPreviousFor(Token other) {
        return this.indexInCode + 1 == other.indexInCode;
    }
    public boolean isNextFor(Token other) {
        return this.indexInCode - 1 == other.indexInCode;
    }

    @Override
    public String toString() {
        String fullClassName = getClass().getSimpleName();
        return fullClassName + "{" + getValue() + "}";
    }

    public abstract Type getType();

    public enum Type {
        NUMBER, STRING, LITERAL, SINGLE_CHAR
    }
}
