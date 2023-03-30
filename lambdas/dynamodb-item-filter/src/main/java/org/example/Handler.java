package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public class Handler implements RequestHandler<Map<String, String>, String> {

//    @Override
//    public String handleRequest(EventBody event, Context context) {
////        LambdaLogger logger = context.getLogger();
////        Gson gson = new Gson();
////        logger.log("EVENT: " + gson.toJson(event));
////
////        String subId = event.getString("subId");
////        String imagesCollectionId = event.getString("imagesCollectionId");
////        String imageIndex = event.getString("imageIndex");
////
////        logger.log("event.getClass().toString();: " + event.getClass().toString());
////        logger.log("subId: " + subId);
////        logger.log("imagesCollectionId: " + imagesCollectionId);
////        logger.log("imageIndex: " + imageIndex);
////
////        return "subId: " + subId + " imagesCollectionId: " + imagesCollectionId + " imageIndex: " + imageIndex + " ";
//        String subId = event.body.subId;
//        String imagesCollectionId = event.body.imagesCollectionId;
//        String imageIndex = event.body.imageIndex;
//        System.out.println("Input received: " + event.toString());
//
//        // Your logic here to process the input
//        return "subId: " + subId + " imagesCollectionId: " + imagesCollectionId + " imageIndex: " + imageIndex + " ";
//    }

    @Override
    public String handleRequest(@RequestBody Map<String, String> event, Context context) {

//        LambdaLogger logger = context.getLogger();
//        logger.log("Handler invoked");
//
//        String subId = event.get("subId");
//        String imagesCollectionId = event.get("imagesCollectionId");
//        String imageIndex = event.get("imageIndex");
//        logger.log("subId: " + subId);
//        logger.log("imagesCollectionId: " + imagesCollectionId);
//        logger.log("imageIndex: " + imageIndex);
//        return "subId: " + subId + " imagesCollectionId: " + imagesCollectionId + " imageIndex: " + imageIndex + " ";
        return "YES 200";
    }

}
