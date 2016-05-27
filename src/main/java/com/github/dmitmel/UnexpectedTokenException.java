package com.github.dmitmel;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(char c) {
        super(Character.toString(c));
    }
}
