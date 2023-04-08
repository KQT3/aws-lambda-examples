package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<EventBody, String> {

    @Override
    public String handleRequest(EventBody event, Context context) {
        String subId = event.getSubId();
        String indexPos = event.getIndexPos();
        LambdaLogger logger = context.getLogger();
        logger.log("subId: " + subId);
        logger.log("indexPos: " + indexPos);
        return subId + " " + indexPos + " ";
    }
}
