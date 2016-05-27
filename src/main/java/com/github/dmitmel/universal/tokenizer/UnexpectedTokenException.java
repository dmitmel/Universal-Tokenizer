package com.github.dmitmel.universal.tokenizer;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(char c) {
        super(Character.toString(c));
    }
}
