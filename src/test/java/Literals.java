import github.dmitmel.universal.tokenizer.*;
import org.junit.Test;

import java.util.List;

public class Literals extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("token1 token2 token3");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"), new LiteralToken("token3"));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void literalStartingWithNumber() {
        new Tokenizer().toTokenList("1token 2token 3token");
    }

    @Test
    public void testParsingLiteralsSeparatedBySingleChars() {
        List<Token> tokens = new Tokenizer().toTokenList("a-b");
        assertTokensEqual(tokens, new LiteralToken("a"), new SingleCharToken('-'), new LiteralToken("b"));
    }

    @Test
    public void testParsingLiteralsWithFirstUnderscore() {
        List<Token> tokens = new Tokenizer().toTokenList("_abc");
        assertTokensEqual(tokens, new LiteralToken("_abc"));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testUnexpectedStringAfterLiteral() {
        new Tokenizer().toTokenList("token\"string\"");
    }

    @Test
    public void testParsingCommentAfterLiteral() {
        List<Token> tokens = new Tokenizer().toTokenList("token1/*multiline comment*/token2// single line comment");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"));
    }
}
