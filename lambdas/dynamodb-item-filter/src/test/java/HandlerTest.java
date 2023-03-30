import com.amazonaws.services.lambda.runtime.Context;
import org.example.EventBody;
import org.example.Handler;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(HandlerTest.class);

    @Test
    void testHandler() {
        logger.info("Invoke TEST - Handler");
        Context context = new TestContext();
        Handler handler = new Handler();
        EventBody.Body body = new EventBody.Body("1", "2", "3");
        EventBody eventBody = new EventBody(body);
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("subId", "1");
        objectObjectHashMap.put("imagesCollectionId", "2");
        objectObjectHashMap.put("imageIndex", "3");
        var s = handler.handleRequest(objectObjectHashMap, context);
        System.out.println(s);
    }

    @Test
    void name() {

    }
}
