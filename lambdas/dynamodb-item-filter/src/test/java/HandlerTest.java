import com.amazonaws.services.lambda.runtime.Context;
import org.example.EventBody;
import org.example.Handler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class HandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(HandlerTest.class);

    @Test
    void testHandler() {
        logger.info("Invoke TEST - Handler");
        var event = List.of(20, 5);
        Context context = new TestContext();
        Handler handler = new Handler();
        EventBody eventBody = new EventBody();
        eventBody.setSubId("1");
        eventBody.setImagesCollectionId("2");
        eventBody.setImageIndex("2");
        var s = handler.handleRequest(eventBody, context);
        System.out.println(s);
    }
}
