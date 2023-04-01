import org.example.JWTHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

class JWTHelperTest {

    @Test
    void getSubId() throws NoSuchAlgorithmException {
        String accessToken = JWTHelper.createTestToken();
        String subId = JWTHelper.getSubId(accessToken);
        System.out.println("subId: " + subId);
        Assertions.assertNotNull(subId);
    }

    @Test
    void createTokenSuccess() throws NoSuchAlgorithmException {
        String token = JWTHelper.createTestToken();
        System.out.println(token);
        Assertions.assertNotNull(token);
    }
}
