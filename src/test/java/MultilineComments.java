import org.junit.Test;
import com.github.dmitmel.universal.tokenizer.LiteralToken;
import com.github.dmitmel.universal.tokenizer.NoEndingSeqInCommentException;
import com.github.dmitmel.universal.tokenizer.Token;
import com.github.dmitmel.universal.tokenizer.Tokenizer;

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
