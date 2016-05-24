import org.junit.Test;
import org.universal.tokenizer.*;

import java.util.List;

public class Literals extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("token1 token2 token3");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"), new LiteralToken("token3"));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void literalStartingWithNumber() {
        new ClassicTokenizer().toTokenList("1token 2token 3token");
    }

    @Test
    public void testParsingLiteralsSeparatedBySingleChars() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("a-b");
        assertTokensEqual(tokens, new LiteralToken("a"), new SingleCharToken('-'), new LiteralToken("b"));
    }

    @Test
    public void testParsingLiteralsWithFirstUnderscore() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("_abc");
        assertTokensEqual(tokens, new LiteralToken("_abc"));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testUnexpectedStringAfterLiteral() {
        new ClassicTokenizer().toTokenList("token\"string\"");
    }

    @Test
    public void testParsingCommentAfterLiteral() {
        List<Token> tokens = new ClassicTokenizer().toTokenList("token1/*multiline comment*/token2// single line comment");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"));
    }
}