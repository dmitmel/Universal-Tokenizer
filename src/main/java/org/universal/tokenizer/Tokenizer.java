package org.universal.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for tokenizing classic (modern) language code, but not Forth.
 */
public class Tokenizer {
    public static final List<Character> SPACERS = Arrays.asList(' ', '\t', '\n', '\r');
    public static final List<Character> NUMBERS = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
    public static final List<Character> LETTERS = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

    private StringBuilder tokenBuilder = new StringBuilder(0);
    private State state = State.NONE;
    private List<Token> tokens = new ArrayList<>(0);
    private String code;
    private int i = 0;

    public String singleLineCommentSequence = "//";
    public String multilineCommentStart = "/*";
    public String multilineCommentEnd = "*/";

    public List<Token> toTokenList(String code) {
        resetFields(code);
        checkFields();

        for (i = 0; i < code.length(); i++) {
            char c = code.charAt(i);

            if (singleLineCommentSequence.charAt(0) == c &&
                    code.regionMatches(i, singleLineCommentSequence, 0, singleLineCommentSequence.length())) {
                addTokenBeforeComment();
                skipAllCharsInSingleLineComment();
                state = State.NONE;
            } else if (multilineCommentStart.charAt(0) == c &&
                    code.regionMatches(i, multilineCommentStart, 0, multilineCommentStart.length())) {
                addTokenBeforeComment();
                skipAllCharsInMultilineComment();
                state = State.NONE;
            } else {
                if (!SPACERS.contains(c) && state == State.NONE) {
                    state = decideCurrentState(c);
                    // The first quote mustn't be added to string using method
                    // {@link Tokenizer#processStringChar(char)}
                    // Because it will decide that this quote is closing
                    if (state == State.STRING) {
                        tokenBuilder.append(c);
                        continue;
                    }
                }

                switch (state) {
                    case LITERAL:
                        processLiteralChar(c);
                        break;

                    case NUMBER:
                        processNumberChar(c);
                        break;

                    case ESCAPED_STRING_CHAR:
                    case STRING:
                        processStringChar(c);
                        break;

                    case SINGLE_CHAR:
                        processSingleChar(c);
                }
            }
        }

        if (state != State.NONE) {
            switch (state) {
                case ESCAPED_STRING_CHAR:
                    throw new NoEscapedCharAfterESCException();
                case STRING:
                    throw new NoClosingQuoteInStringException();
                case NUMBER:
                    tokens.add(new NumberToken(tokenBuilder.toString(), i));
                    break;
                case LITERAL:
                    tokens.add(new LiteralToken(tokenBuilder.toString(), i));
                    break;
            }
        }

        return tokens;
    }

    private void checkFields() {
        assert singleLineCommentSequence.length() > 0 :
                "Length of Tokenizer#singleLineCommentSequence must be greater than zero";
        assert multilineCommentStart.length() > 0 :
                "Length of Tokenizer#multilineCommentStart must be greater than zero";
        assert !singleLineCommentSequence.equals(multilineCommentStart) :
                "Tokenizer#singleLineCommentSequence mustn\'t equal Tokenizer#multilineCommentStart";
        assert multilineCommentEnd.length() > 0 :
                "Length of Tokenizer#multilineCommentEnd must be greater than zero";
    }

    private void processStringChar(char c) {
        if (c == '\\' && state != State.ESCAPED_STRING_CHAR) {
            state = State.ESCAPED_STRING_CHAR;
        } else {
            if (state != State.ESCAPED_STRING_CHAR && c == '\"') {
                tokenBuilder.append(c);
                addTokenInList(new StringToken(tokenBuilder.toString(), i));
            } else if (state == State.ESCAPED_STRING_CHAR) {
                processEscapedCharInString(c);
            } else
                tokenBuilder.append(c);
        }
    }

    private void processEscapedCharInString(char c) {
        switch (c) {
            case 'n':
                tokenBuilder.append('\n');
                break;
            case 'r':
                tokenBuilder.append('\r');
                break;
            case '\\':
                tokenBuilder.append('\\');
                break;
            case '\'':
                tokenBuilder.append('\'');
                break;
            case '\"':
                tokenBuilder.append('\"');
                break;
        }
    }

    private void processSingleChar(char c) {
        addTokenInList(new SingleCharToken(c, i));
    }

    private void processLiteralChar(char c) {
        if (SPACERS.contains(c)) {
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
        } else if (LETTERS.contains(c) || NUMBERS.contains(c) || c == '_') {
            tokenBuilder.append(c);
        } else if (c != '\"') {
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
            tokens.add(new SingleCharToken(c, i));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void processNumberChar(char c) {

        if (SPACERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
        } else if (NUMBERS.contains(c) || c == '-') {
            tokenBuilder.append(c);
        } else if (!LETTERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
            tokens.add(new SingleCharToken(c, i));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void addTokenBeforeComment() {
        switch (state) {
            case LITERAL:
                tokens.add(new LiteralToken(tokenBuilder.toString(), i));
                break;
            case NUMBER:
                tokens.add(new NumberToken(tokenBuilder.toString(), i));
                break;
            case STRING:
                tokens.add(new StringToken(tokenBuilder.toString(), i));
                break;
        }
        tokenBuilder = new StringBuilder(0);
        // Calling code has already set state to State#NONE
    }

    private void resetFields(String code) {
        tokenBuilder = new StringBuilder(0);
        state = State.NONE;
        tokens = new ArrayList<>(0);
        this.code = code;
    }

    private void skipAllCharsInSingleLineComment() {
        for (; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '\n' || c == '\r')
                break;
        }
    }

    private void skipAllCharsInMultilineComment() {
        for (; i < code.length(); i++) {
            if (code.regionMatches(i, multilineCommentEnd, 0, multilineCommentEnd.length())) {
                i += multilineCommentEnd.length() - 1;
                break;
            }
        }

        if (i == code.length())
            throw new NoEndingSeqInCommentException();
    }

    private void addTokenInList(Token token) {
        tokens.add(token);
        tokenBuilder = new StringBuilder(0);
        state = State.NONE;
    }

    private State decideCurrentState(char c) {
        if (c == '\"')
            return State.STRING;
        else if (LETTERS.contains(c) || c == '_')
            return State.LITERAL;
        else if (NUMBERS.contains(c))
            return State.NUMBER;
        else // If reached this place, "c" won't be SPACER
            return State.SINGLE_CHAR;
    }

    private enum State {
        NONE, LITERAL, NUMBER, STRING, SINGLE_CHAR, ESCAPED_STRING_CHAR
    }
}
