import org.junit.Assert;
import com.github.dmitmel.Token;

import java.util.Arrays;
import java.util.List;

class TokenTester extends Assert {
    void assertTokensEqual(List<Token> actual, Token... expected) {
        // 1000 lvl hack :)
        assertEquals(Arrays.asList(expected).toString(), actual.toString());
    }

    void assertTokensNotEqual(List<Token> actual, Token... expected) {
        assertNotEquals(Arrays.asList(expected).toString(), actual.toString());
    }
}
