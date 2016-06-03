import org.junit.Assert;
import github.dmitmel.universal.tokenizer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TokenTester extends Assert {
    private List<Token> filterTokensWithoutComments(List<Token> tokens) {
        List<Token> filtered = new ArrayList<>(0);
        for (Token token : tokens)
            if (token.getType() != Token.Type.COMMENT)
                filtered.add(token);
        return filtered;
    }

    void assertTokensEqual(List<Token> actual, Token... expected) {
        // 1000 lvl hack :)
        assertEquals(Arrays.asList(expected).toString(), filterTokensWithoutComments(actual).toString());
    }

    void assertTokensNotEqual(List<Token> actual, Token... expected) {
        assertNotEquals(Arrays.asList(expected).toString(), filterTokensWithoutComments(actual).toString());
    }
}
