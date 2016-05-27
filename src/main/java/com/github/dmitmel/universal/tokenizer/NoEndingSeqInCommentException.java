package com.github.dmitmel.universal.tokenizer;

/**
 * Exception can be thrown if multiline comment hasn't got ending sequence. For example:
 *
 * <pre><code>
 * /* This is opening sequence
 * Some text,
 * but no closing sequence
 * </code></pre>
 *
 * Here opening sequence is {@code /*} and closing is <code>*&#x002F;</code>.
 */
public class NoEndingSeqInCommentException extends TokenParseException {
    public NoEndingSeqInCommentException() {
        super();
    }
}
