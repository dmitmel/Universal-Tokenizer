package org.universal.tokenizer;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(char c) {
        super(Character.toString(c));
    }
}
