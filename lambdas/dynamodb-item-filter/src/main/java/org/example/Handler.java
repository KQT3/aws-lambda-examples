package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.json.JSONObject;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<APIGatewayV2HTTPResponse, QueryResponse> {
    private static final String TABLE_NAME = "user_images";

    @Override
    public QueryResponse handleRequest(APIGatewayV2HTTPResponse event, Context context) {
        LambdaLogger logger = context.getLogger();

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

        var itemFromDynamoDB = getItemFiltered(subId, imagesCollectionId, imageIndex);
//        logger.log("itemFromDynamoDB: " + itemFromDynamoDB);
//        logger.log("itemFromDynamoDB.hasItems(): " + itemFromDynamoDB.hasItems());
//        logger.log("itemFromDynamoDB.items(): " + itemFromDynamoDB.items());
//        List<Map<String, AttributeValue>> items = itemFromDynamoDB.items();
        List<Map<String, AttributeValue>> items = itemFromDynamoDB.items();
        return items;
    }

    public QueryResponse getItemFiltered(String userId, String imagesCollectionId, String imageIndex) {
        var sdkHttpClient = ApacheHttpClient.builder().build();
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(sdkHttpClient)
                .build();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", AttributeValue.builder().s(userId).build());
        expressionAttributeValues.put(":imagesCollectionId", AttributeValue.builder().s(imagesCollectionId).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(expressionAttributeValues)
                .filterExpression("contains(imagesCollection[" + imageIndex + "].imagesCollectionId, :imagesCollectionId)")
                .projectionExpression("imagesCollection[" + imageIndex + "]")
                .build();

        return dynamoDbClient.query(queryRequest);
    }

//    public void convert(List<Map<String, AttributeValue>> items) {
//        ImagesCollection imagesCollection = items.stream().map(item -> {
//
//            );
//        }).collect(Collectors.toList());
//    }

}
