package github.dmitmel.universal.tokenizer;

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
    public static final List<String> SHORT_STRING_BOUNDS_SEQUENCES = Arrays.asList("\'", "\"");
    public static final List<String> LONG_STRING_BOUNDS_SEQUENCES = Arrays.asList("\'\'\'", "\"\"\"");
    public static final List<String> STRING_BOUNDS_SEQUENCES = Arrays.asList("\"\"\"", "\'\'\'", "\"", "\'");

    private StringBuilder tokenBuilder = new StringBuilder(0);
    private State state = State.NONE;
    private List<Token> tokens = new ArrayList<>(0);
    private String code;
    private int i = 0;
    private String foundStringBoundsSequence;
    private String usedStringStartSequence;

    public String singleLineCommentSequence = "//";
    public String multilineCommentStart = "/*";
    public String multilineCommentEnd = "*/";
    /** List for additional literal chars. You can add, for example, math operators, and your literals would look
     * like Scala's function/class/... names. */
    public List<Character> additionalLiteralCharsList = new ArrayList<>(0);

    public List<Token> toTokenList(String code) {
        resetFields();
        this.code = code;
        checkFields();

        for (i = 0; i < code.length(); i++) {
            char c = code.charAt(i);

            foundStringBoundsSequence = findStringBoundsSequence();

            if (!SPACERS.contains(c) && state == State.NONE) {
                state = decideCurrentState(c);

                if (state == State.NUMBER) {
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

                case STRING_STARTING_SEQUENCE:
                    usedStringStartSequence = foundStringBoundsSequence;
                case ESCAPED_STRING_CHAR:
                case STRING_BODY:
                    processStringChar(c);
                    break;

                case SINGLE_CHAR:
                    processSingleChar(c);
                    break;

                case SINGLE_LINE_COMMENT:
                    processSingleLineCommentChar(c);
                    break;

                case MULTILINE_COMMENT:
                    processMultilineCommentChar(c);
                    break;
            }
        }

        if (state != State.NONE) {
            switch (state) {
                case ESCAPED_STRING_CHAR:
                    throw new NoEscapedCharAfterESCException();
                case STRING_BODY:
                    throw new NoClosingQuoteInStringException();
                case NUMBER:
                    tokens.add(new NumberToken(tokenBuilder.toString(), i));
                    break;
                case LITERAL:
                    tokens.add(new LiteralToken(tokenBuilder.toString(), i));
                    break;
                case MULTILINE_COMMENT:
                    throw new NoEndingSeqInCommentException();
            }
        }

        return tokens;
    }

    private void checkFields() {
        assert singleLineCommentSequence.length() > 0 :
                "length of Tokenizer#singleLineCommentSequence must be greater than zero";
        assert multilineCommentStart.length() > 0 :
                "length of Tokenizer#multilineCommentStart must be greater than zero";
        assert !singleLineCommentSequence.equals(multilineCommentStart) :
                "Tokenizer#singleLineCommentSequence mustn\'t equal Tokenizer#multilineCommentStart";
        assert multilineCommentEnd.length() > 0 :
                "length of Tokenizer#multilineCommentEnd must be greater than zero";
    }

    private void processStringChar(char c) {
        if (c == '\\' && state != State.ESCAPED_STRING_CHAR) {
            state = State.ESCAPED_STRING_CHAR;
        } else {
            if (state != State.ESCAPED_STRING_CHAR && state != State.STRING_STARTING_SEQUENCE
                    && usedStringStartSequence.equals(foundStringBoundsSequence)) {
                addStringBoundsToBuilder();
                addTokenInList(new StringToken(tokenBuilder.toString(), i));
            } else if (state != State.ESCAPED_STRING_CHAR && state != State.STRING_STARTING_SEQUENCE &&
                    SHORT_STRING_BOUNDS_SEQUENCES.contains(usedStringStartSequence) &&
                    LONG_STRING_BOUNDS_SEQUENCES.contains(foundStringBoundsSequence)) {
                throw new UnexpectedTokenException(c);
            } else if (state == State.STRING_STARTING_SEQUENCE) {
                addStringBoundsToBuilder();
                state = State.STRING_BODY;
            } else
                tokenBuilder.append(c);
        }
    }

    private void addStringBoundsToBuilder() {
        // Number 1 has been already added to i, so we remove it here
        tokenBuilder.append(foundStringBoundsSequence);
        i += foundStringBoundsSequence.length() - 1;
    }

    private void processSingleChar(char c) {
        addTokenInList(new SingleCharToken(c, i));
    }

    private void processLiteralChar(char c) {
        if (SPACERS.contains(c)) {
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
        } else if (isThereACommentAfterI()) {
            tokenBuilder.append(c);
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
        } else if (DEFAULT_LITERAL_CHARS.contains(c) || NUMBER_CHARS.contains(c) ||
                additionalLiteralCharsList.contains(c)) {
            tokenBuilder.append(c);
        } else if (foundStringBoundsSequence == null) {
            addTokenInList(new LiteralToken(tokenBuilder.toString(), i));
            tokens.add(new SingleCharToken(c, i));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void processNumberChar(char c) {
        if (SPACERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
        } else if (isThereACommentAfterI()) {
            tokenBuilder.append(c);
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
        } else if (NUMBER_CHARS.contains(c)) {
            tokenBuilder.append(c);
        } else if (!DEFAULT_LITERAL_CHARS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString(), i));
            tokens.add(new SingleCharToken(c, i));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void resetFields() {
        tokenBuilder = new StringBuilder(0);
        state = State.NONE;
        tokens = new ArrayList<>(0);
    }

    private void processSingleLineCommentChar(char c) {
        if (c == '\n' || (c == '\r' && getNextChar() == '\n')) {
            addTokenInList(new CommentToken(tokenBuilder.toString()));
            state = State.NONE;
        } else
            tokenBuilder.append(c);
    }

    private void processMultilineCommentChar(char c) {
        if (code.regionMatches(i, multilineCommentEnd, 0, multilineCommentEnd.length())) {
            addTokenInList(new CommentToken(tokenBuilder.toString()));
            i += multilineCommentEnd.length() - 1;
            state = State.NONE;
        } else
            tokenBuilder.append(c);
    }

    private void addTokenInList(Token token) {
        tokens.add(token);
        tokenBuilder = new StringBuilder(0);
        state = State.NONE;
    }

    private State decideCurrentState(char c) {
        State state;

        if (foundStringBoundsSequence != null) {
            state = State.STRING_STARTING_SEQUENCE;
        } else if (DEFAULT_LITERAL_CHARS.contains(c) || additionalLiteralCharsList.contains(c)) {
            state = State.LITERAL;
        } else if (NUMBER_CHARS.contains(c)) {
            state = State.NUMBER;
        } else if (code.regionMatches(i, singleLineCommentSequence, 0, singleLineCommentSequence.length())) {
            state = State.SINGLE_LINE_COMMENT;
        } else if (code.regionMatches(i, multilineCommentStart, 0, multilineCommentStart.length())) {
            state = State.MULTILINE_COMMENT;
        } else {    // If reached this place, "c" won't be spacer
            state = State.SINGLE_CHAR;
        }

        return state;
    }

    private String findStringBoundsSequence() {
        for (String sequence : STRING_BOUNDS_SEQUENCES)
            if (code.regionMatches(i, sequence, 0, sequence.length()))
                return sequence;
        return null;
    }

    private boolean isThereACommentAfterI() {
        return  code.regionMatches(i + 1, singleLineCommentSequence, 0, singleLineCommentSequence.length()) ||
                code.regionMatches(i + 1, multilineCommentStart, 0, multilineCommentStart.length());
    }

    private int getNextChar() {
        return (i + 1 < code.length()) ? code.charAt(i) : -1;
    }

    private enum State {
        /** State is used while reading only {@link #SPACERS}, and waiting for another lexeme/token. */
        NONE,

        LITERAL,

        NUMBER,

        STRING_BODY,

        STRING_STARTING_SEQUENCE,

        /** State is used if there's ESC character in string, and waiting for escaped. */
        ESCAPED_STRING_CHAR,

        SINGLE_CHAR,

        SINGLE_LINE_COMMENT,

        MULTILINE_COMMENT
    }
}
