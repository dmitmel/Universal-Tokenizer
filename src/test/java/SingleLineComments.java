import org.junit.Test;
import github.dmitmel.universal.tokenizer.LiteralToken;
import github.dmitmel.universal.tokenizer.Token;
import github.dmitmel.universal.tokenizer.Tokenizer;

import java.util.List;

public class SingleLineComments extends TokenTester {
    @Test
    public void simpleTest() {
        List<Token> tokens = new Tokenizer().toTokenList("token1 token2 token3 // This is comment! token4");
        assertTokensEqual(tokens, new LiteralToken("token1"), new LiteralToken("token2"), new LiteralToken("token3"));
    }
}
