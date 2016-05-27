package com.github.dmitmel.universal.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for tokenizing classic (modern) language code, but not Forth.
 */
public class Tokenizer {
    public static final List<Character> SPACERS = Arrays.asList(' ', '\t', '\n', '\r');

    public static final List<Character> NUMBER_CHARS = Arrays.asList(
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0');

    public static final List<Character> DEFAULT_LITERAL_CHARS = Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '_', '$');
    /** List for additional literal chars. You can add, for example, math operators, and your literals would look
     * like Scala's function/class/... names. */
    public List<Character> additionalLiteralCharsList = new ArrayList<>(0);

    public static final List<String> STRING_BOUND_SEQUENCES = Arrays.asList("\"\"\"", "\'\'\'", "\"", "\'");

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
                    // Because method will decide, that this is closing quote
                    if (state == State.STRING) {
                        addStringBoundsToBuilder();
                        continue;

                    } else if (state == State.NUMBER) {
                        Token prevToken = (tokens.size() > 0) ? tokens.get(tokens.size() - 1) : null;
                        Token prevPrevToken = (tokens.size() > 1) ? tokens.get(tokens.size() - 2) : null;

                        if (prevPrevToken == null) {
                            if (prevToken != null && prevToken.getType() == Token.Type.SINGLE_CHAR &&
                                    prevToken.getIndexInCode() + 1 == i && (char) prevToken.getValue() == '-') {
                                tokens.remove(tokens.size() - 1);
                                tokenBuilder.append('-');
                            }
                        } else if (prevPrevToken.getType() == Token.Type.SINGLE_CHAR &&
                                (char) prevPrevToken.getValue() == '-') {
                            if (prevToken != null && prevToken.getType() == Token.Type.SINGLE_CHAR &&
                                    prevToken.getIndexInCode() + 1 == i && (char) prevToken.getValue() == '-') {
                                tokens.remove(tokens.size() - 1);
                                tokenBuilder.append('-');
                            }
                        }
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
            if (state != State.ESCAPED_STRING_CHAR && anyStringBoundsSequenceMatches()) {
                addStringBoundsToBuilder();
                addTokenInList(new StringToken(tokenBuilder.toString(), i));
            } else if (state == State.ESCAPED_STRING_CHAR) {
                processEscapedCharInString(c);
            } else
                tokenBuilder.append(c);
        }
    }

    private void addStringBoundsToBuilder() {
        for (String sequence : STRING_BOUND_SEQUENCES) {
            if (code.regionMatches(i, sequence, 0, sequence.length())) {
                // Number 1 has been already added to i, so we remove it here
                tokenBuilder.append(sequence);
                i += sequence.length() - 1;
                break;
            }
        }
    }

    private void processEscapedCharInString(char c) {
        switch (c) {
            case 't':
                tokenBuilder.append('\t');
                break;
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
        } else if (DEFAULT_LITERAL_CHARS.contains(c) || NUMBER_CHARS.contains(c) ||
                additionalLiteralCharsList.contains(c)) {
            tokenBuilder.append(c);
        } else if (!anyStringBoundsSequenceMatches()) {
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
            tokens.add(new SingleCharToken(c, i));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void processNumberChar(char c) {
        if (SPACERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
        } else if (NUMBER_CHARS.contains(c)) {
            tokenBuilder.append(c);
        } else if (!DEFAULT_LITERAL_CHARS.contains(c)) {
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
        State state;

        if (anyStringBoundsSequenceMatches()) {
            state = State.STRING;
        } else if (DEFAULT_LITERAL_CHARS.contains(c) || additionalLiteralCharsList.contains(c)) {
            state = State.LITERAL;
        } else if (NUMBER_CHARS.contains(c)) {
            state = State.NUMBER;
        } else {    // If reached this place, "c" won't be SPACER
            state = State.SINGLE_CHAR;
        }

        return state;
    }

    private boolean anyStringBoundsSequenceMatches() {
        for (String sequence : STRING_BOUND_SEQUENCES)
            if (code.regionMatches(i, sequence, 0, sequence.length()))
                return true;
        return false;
    }

    private enum State {
        /** State is used while reading only {@link #SPACERS}, and waiting for another lexeme/token. */
        NONE,

        LITERAL,

        NUMBER,

        STRING,

        SINGLE_CHAR,
        /** State is used if there's ESC character in string, and waiting for escaped. */
        ESCAPED_STRING_CHAR
    }
}
