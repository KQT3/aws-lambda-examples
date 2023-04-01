import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.example.Handler;
import org.example.JWTHelper;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class HandlerTest {
    Context context = new TestContext();
    Handler handler = new Handler();
    final String AUTHORIZATION_HEADER = "authorization";

    @Test
    public void testHandleRequest() throws NoSuchAlgorithmException {
        //given
        String ACCESS_TOKEN = JWTHelper.createTestToken();

        Map<String, String> eventBody = new HashMap<>();
        eventBody.put("subId", "1");
        eventBody.put("imagesCollectionId", "2");
        eventBody.put("imageIndex", "3");

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "Bearer " + ACCESS_TOKEN);
        headers.put("Content-Type", "application/json");

        APIGatewayV2HTTPResponse request = APIGatewayV2HTTPResponse.builder()
                .withHeaders(headers)
                .withBody(new Gson().toJson(eventBody))
                .build();

        // when
        String result = handler.handleRequest(request, context);

        System.out.println(result);
    }
}
