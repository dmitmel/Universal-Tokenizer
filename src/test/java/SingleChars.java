import org.junit.Test;
import org.universal.tokenizer.*;

import java.util.List;

public class SingleChars extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("? : +");
        assertTokensEqual(tokens, new SingleCharToken('?'), new SingleCharToken(':'), new SingleCharToken('+'));
    }

    @Test
    public void testParsingCharsWithNoSeparators() {
        List<Token> tokens = new Tokenizer().toTokenList(":-?");
        assertTokensEqual(tokens, new SingleCharToken(':'), new SingleCharToken('-'), new SingleCharToken('?'));
    }

    @Test
    public void testParsingCommentAfterSingleChars() {
        List<Token> tokens = new Tokenizer().toTokenList("+/*multiline comment -*/?// single line comment :");
        assertTokensEqual(tokens, new SingleCharToken('+'), new SingleCharToken('?'));
    }
}
