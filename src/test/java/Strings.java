import org.junit.Test;
import org.universal.tokenizer.*;

import java.util.List;

public class Strings extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("\"str1\" \"str2\" \"str3\"");
        assertTokensEqual(tokens,
                new StringToken("\"str1\""), new StringToken("\"str2\""), new StringToken("\"str3\""));
    }

    @Test
    public void testParsingStringsSeparatedBySingleChars() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("\"abc\"-\"def\"");
        assertTokensEqual(tokens, new StringToken("\"abc\""), new SingleCharToken('-'), new StringToken("\"def\""));
    }

    @Test
    public void testParsingCommentAfterLiteral() {
        List<Token> tokens = new ClassicTokenizer().toTokenList(
                "\"1\"/*multiline comment \"2\"*/\"3\"// single line comment \"4\"");
        assertTokensEqual(tokens, new StringToken("\"1\""), new StringToken("\"3\""));
    }
}
