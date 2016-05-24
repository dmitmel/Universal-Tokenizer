package org.universal.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class ClassicTokenizer implements Tokenizer {

    private StringBuilder tokenBuilder = new StringBuilder(0);
    private State state = State.NONE;
    private List<Token> tokens = new ArrayList<>(0);
    private boolean charInStringIsEscaped = false;
    private String code;
    private int i = 0;

    private String singleLineCommentSequence = "//";
    @Override
    public String getSingleLineCommentSequence() {
        return singleLineCommentSequence;
    }
    @Override
    public void setSingleLineCommentSequence(String singleLineCommentSequence) {
        assert singleLineCommentSequence.length() > 0 :
                "Length of Tokenizer#singleLineCommentSequence must be greater than zero";
        assert !singleLineCommentSequence.equals(multilineCommentStart) :
                "Tokenizer#singleLineCommentSequence mustn\'t equal Tokenizer#multilineCommentStart";
        this.singleLineCommentSequence = singleLineCommentSequence;
    }

    private String multilineCommentStart = "/*";
    @Override
    public String getMultilineCommentStart() {
        return multilineCommentStart;
    }
    @Override
    public void setMultilineCommentStart(String multilineCommentStart) {
        assert multilineCommentStart.length() > 0 :
                "Length of Tokenizer#multilineCommentStart must be greater than zero";
        assert !singleLineCommentSequence.equals(multilineCommentStart) :
                "Tokenizer#singleLineCommentSequence mustn\'t equal Tokenizer#multilineCommentStart";
        this.multilineCommentStart = multilineCommentStart;
    }

    private String multilineCommentEnd = "*/";
    @Override
    public String getMultilineCommentEnd() {
        return multilineCommentEnd;
    }
    @Override
    public void setMultilineCommentEnd(String multilineCommentEnd) {
        assert multilineCommentEnd.length() > 0 :
                "Length of Tokenizer#multilineCommentEnd must be greater than zero";
        this.multilineCommentEnd = multilineCommentEnd;
    }

    @Override
    public List<Token> toTokenList(String code) {
        resetFields(code);

        for (i = 0; i < code.length(); i++) {
            char c = code.charAt(i);

            if (singleLineCommentSequence.charAt(0) == c && isThereSequenceAfterI(singleLineCommentSequence)) {
                addTokenBeforeComment();
                skipAllCharsInSingleLineComment();
                state = State.NONE;
            } else if (multilineCommentStart.charAt(0) == c && isThereSequenceAfterI(multilineCommentStart)) {
                addTokenBeforeComment();
                skipAllCharsInMultilineComment();
                state = State.NONE;
            } else {
                if (!SPACERS.contains(c) && state == State.NONE) {
                    state = decideCurrentState(c);
                    // The first quote mustn't be added to string using method
                    // {@link ClassicTokenizer#processStringChar(char)}
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
                case STRING:
                    throw new TokenParseException("Missing closing quote for string");
                case NUMBER:
                    tokens.add(new NumberToken(tokenBuilder.toString()));
                    break;
                case LITERAL:
                    tokens.add(new LiteralToken(tokenBuilder.toString()));
                    break;
            }
        }

        return tokens;
    }

    private void processStringChar(char c) {
        if (c == '\\' && !charInStringIsEscaped) {
            charInStringIsEscaped = true;
        } else {
            if (!charInStringIsEscaped && c == '\"') {
                tokenBuilder.append(c);
                addTokenInList(new StringToken(tokenBuilder.toString()));
            } else if (charInStringIsEscaped) {
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
        addTokenInList(new SingleCharToken(c));
    }

    private void processLiteralChar(char c) {
        if (SPACERS.contains(c)) {
            addTokenInList(new LiteralToken(tokenBuilder.toString()));
        } else if (LETTERS.contains(c) || NUMBERS.contains(c) || c == '_') {
            tokenBuilder.append(c);
        } else if (c != '\"') {
            addTokenInList(new LiteralToken(tokenBuilder.toString()));
            tokens.add(new SingleCharToken(c));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void processNumberChar(char c) {

        if (SPACERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString()));
        } else if (NUMBERS.contains(c)) {
            tokenBuilder.append(c);
        } else if (!LETTERS.contains(c)) {
            addTokenInList(new NumberToken(tokenBuilder.toString()));
            tokens.add(new SingleCharToken(c));
        } else
            throw new UnexpectedTokenException(c);
    }

    private void addTokenBeforeComment() {
        switch (state) {
            case LITERAL:
                tokens.add(new LiteralToken(tokenBuilder.toString()));
                break;
            case NUMBER:
                tokens.add(new NumberToken(tokenBuilder.toString()));
                break;
            case STRING:
                tokens.add(new StringToken(tokenBuilder.toString()));
                break;
        }
        tokenBuilder = new StringBuilder(0);
        // Calling code has already set state to State#NONE
    }

    private void resetFields(String code) {
        tokenBuilder = new StringBuilder(0);
        state = State.NONE;
        tokens = new ArrayList<>(0);
        charInStringIsEscaped = false;
        this.code = code;
    }

    private boolean isThereSequenceAfterI(String sequence) {
        boolean foundEnd = false;
        boolean stopped = false;

        for (int j = i; j < code.length() && !stopped && !foundEnd; j++) {
            int sequenceI = j - i;
            if (sequence.charAt(sequenceI) == code.charAt(j)) {
                if (sequenceI + 1 == sequence.length())
                    foundEnd = true;
            } else
                stopped = true;
        }

        return foundEnd;
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
            if (isThereSequenceAfterI(multilineCommentEnd)) {
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
        NONE, LITERAL, NUMBER, STRING, SINGLE_CHAR
    }
}
