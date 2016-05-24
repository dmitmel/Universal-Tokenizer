package org.universal.tokenizer;

import java.util.Arrays;
import java.util.List;

public abstract class Tokenizer {
    static final List<Character> LETTERS = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z');
    static final List<Character> NUMBERS = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0');
    static final List<Character> SPACERS = Arrays.asList(' ', '\t', '\n', '\r');

    public String singleLineCommentSequence = "//";
    public String multilineCommentStart = "/*";
    public String multilineCommentEnd = "*/";

    public abstract List<Token> toTokenList(String code);
}
