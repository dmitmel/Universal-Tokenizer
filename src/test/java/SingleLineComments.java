import org.junit.Test;
import org.universal.tokenizer.LiteralToken;
import org.universal.tokenizer.Token;
import org.universal.tokenizer.Tokenizer;

import java.util.List;

public class SingleLineComments extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("token1 token2 token3 // This is comment! token4");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"), new LiteralToken("token3"));
    }
}
