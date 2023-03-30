package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import org.springframework.web.bind.annotation.RequestBody;

public class Handler implements RequestHandler<EventBody, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public String handleRequest(EventBody event, Context context) {
        LambdaLogger logger = context.getLogger();
        String subId = event.getSubId();
        String imagesCollectionId = event.getImagesCollectionId();
        String imageIndex = event.getImageIndex();
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("subId: " + subId);
        logger.log("imagesCollectionId: " + imagesCollectionId);
        logger.log("imageIndex: " + imageIndex);

        return "subId: " + subId + " imagesCollectionId: " + imagesCollectionId + " imageIndex: " + imageIndex + " ";
    }
}
