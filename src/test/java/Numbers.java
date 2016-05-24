import org.junit.Test;
import org.universal.tokenizer.*;

import java.util.List;

public class Numbers extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("123 456 789");
        assertTokensEqual(tokens, new NumberToken("123"), new NumberToken("456"), new NumberToken("789"));
    }

    @Test
    public void testParsingTokenWithFirstUnderscore() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("_123");
        assertTokensNotEqual(tokens, new LiteralToken("_"), new NumberToken("123"));
    }

    @Test
    public void testParsingLiteralsSeparatedBySingleChars() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("1-2");
        assertTokensEqual(tokens, new NumberToken("1"), new SingleCharToken('-'), new NumberToken("2"));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testUnexpectedStringAfterLiteral() {
        new ClassicTokenizer().toTokenList("123\"string\"");
    }

    @Test
    public void testParsingCommentAfterLiteral() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("1/*multiline comment 3*/4// single line comment 5");
        assertTokensEqual(tokens, new NumberToken("1"), new NumberToken("4"));
    }
}
