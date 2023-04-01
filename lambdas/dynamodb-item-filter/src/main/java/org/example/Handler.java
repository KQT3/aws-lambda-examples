package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

public class Handler implements RequestHandler<APIGatewayV2HTTPResponse, String> {

    @Override
    public String handleRequest(@RequestBody APIGatewayV2HTTPResponse event, Context context) {
        LambdaLogger logger = context.getLogger();
//        logger.log(event.getBody());
//        logger.log(event.getHeaders().toString());

        JSONObject headers = new JSONObject(event.getHeaders());
        String authorization = headers.getString("authorization");
        String token = authorization.replace("Bearer ", "");
        String subId = JWTHelper.getSubId(token);

        JSONObject body = new JSONObject(event.getBody());
        String imagesCollectionId = body.getString("imagesCollectionId");
        String imageIndex = body.getString("imageIndex");

        logger.log("subId: " + subId);
        logger.log("imagesCollectionId: " + imagesCollectionId);
        logger.log("imageIndex: " + imageIndex);

        return "subId: " + subId + " imagesCollectionId: " + imagesCollectionId + " imageIndex: " + imageIndex + " funkar ";
    }

}
