package org.universal.tokenizer;

public class TokenParseException extends RuntimeException {
    public TokenParseException(String message) {
        super(message);
    }
    public TokenParseException() {
        super();
    }
    public TokenParseException(String message, Throwable cause) {
        super(message, cause);
    }
    public TokenParseException(Throwable cause) {
        super(cause);
    }
}
