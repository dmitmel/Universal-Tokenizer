import org.junit.Test;
import org.universal.tokenizer.LiteralToken;
import org.universal.tokenizer.NoEndingSeqInCommentException;
import org.universal.tokenizer.Token;
import org.universal.tokenizer.Tokenizer;

import java.util.List;

public class MultilineComments extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("token1 token2 /* This is comment! token3 */ token4");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"), new LiteralToken("token4"));
    }

    @Test(expected = NoEndingSeqInCommentException.class)
    public void parsingWithoutClosingSequenceTest() {
        new Tokenizer().toTokenList("token1 token2 /* This is comment! token3 token4");
    }
}
