package org.universal.tokenizer;

import java.util.Arrays;
import java.util.List;

public interface Tokenizer {
    List<Character> LETTERS = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z');
    List<Character> NUMBERS = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
    List<Character> SPACERS = Arrays.asList(' ', '\t', '\n', '\r');

    String getSingleLineCommentSequence();
    void setSingleLineCommentSequence(String singleLineCommentSequence);

    String getMultilineCommentStart();
    void setMultilineCommentStart(String multilineCommentStart);

    String getMultilineCommentEnd();
    void setMultilineCommentEnd(String multilineCommentEnd);

    List<Token> toTokenList(String code);
}
