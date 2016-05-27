import com.github.dmitmel.*;
import org.junit.Test;

import java.util.List;

public class Strings extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("\"str1\" \"str2\" \"str3\"");
        assertTokensEqual(tokens,
                new StringToken("\"str1\""), new StringToken("\"str2\""), new StringToken("\"str3\""));
    }

    @Test
    public void testParsingStringsSeparatedBySingleChars() {
        List<Token> tokens = new Tokenizer().toTokenList("\"abc\"-\"def\"");
        assertTokensEqual(tokens, new StringToken("\"abc\""), new SingleCharToken('-'), new StringToken("\"def\""));
    }

    @Test
    public void testParsingCommentAfterLiteral() {
        List<Token> tokens = new Tokenizer().toTokenList(
                "\"1\"/*multiline comment \"2\"*/\"3\"// single line comment \"4\"");
        assertTokensEqual(tokens, new StringToken("\"1\""), new StringToken("\"3\""));
    }

    @Test(expected = NoClosingQuoteInStringException.class)
    public void testParsingStringWithMissingClosingQuote() {
        new Tokenizer().toTokenList("\"str");
    }

    @Test(expected = NoEscapedCharAfterESCException.class)
    public void testParsingStringWithNoCharAfterEsc() {
        new Tokenizer().toTokenList("\"str\\");
    }

    @Test
    public void testStringWithTripleQuotes() {
        List<Token> tokens = new Tokenizer().toTokenList("\'\'\'string\'\'\' \"\"\"string\"\"\"");
        assertTokensEqual(tokens, new StringToken("\'\'\'string\'\'\'"), new StringToken("\"\"\"string\"\"\""));
    }
}
