package org.universal.tokenizer;

/**
 * This exception can be thrown if there's ESC char ({@code \}), but there're no char to be escaped.
 */
public class NoEscapedCharAfterESCException extends TokenParseException {
    public NoEscapedCharAfterESCException() {
        super();
    }
}
