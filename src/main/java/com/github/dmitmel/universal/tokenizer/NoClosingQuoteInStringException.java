package com.github.dmitmel.universal.tokenizer;

/**
 * This exception can be thrown if string doesn't contain closing quote.
 */
public class NoClosingQuoteInStringException extends TokenParseException {
    public NoClosingQuoteInStringException() {
        super();
    }
}
