package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.example.dto.ImageDTO;
import org.json.JSONObject;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<APIGatewayV2HTTPResponse, ImageDTO> {
    private static final String TABLE_NAME = "user_images";

    @Override
    public ImageDTO handleRequest(APIGatewayV2HTTPResponse event, Context context) {
        LambdaLogger logger = context.getLogger();

        JSONObject headers = new JSONObject(event.getHeaders());
        String authorization = headers.getString("authorization");
        String token = authorization.replace("Bearer ", "");
        String subId = JWTHelper.getSubId(token);

        JSONObject body = new JSONObject(event.getBody());
        String imageIndex = body.getString("imageIndex");

        logger.log("subId: " + subId);
        logger.log("imageIndex: " + imageIndex);

        var itemFromDynamoDB = getItemFiltered(subId, imageIndex);
        return toDTO(itemFromDynamoDB.items());
    }

    public QueryResponse getItemFiltered(String userId, String imageIndex) {
        var sdkHttpClient = ApacheHttpClient.builder().build();
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(sdkHttpClient)
                .build();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userId", AttributeValue.builder().s(userId).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(expressionAttributeValues)
                .projectionExpression("imagesCollection[" + imageIndex + "]")
                .build();

        return dynamoDbClient.query(queryRequest);
    }

    public ImageDTO toDTO(List<Map<String, AttributeValue>> items) {
        return items.stream()
                .flatMap(item -> item.get("imagesCollection").l().stream().map(image -> {
                    var imagesCollectionId = image.m().get("imagesCollectionId").s();
                    var timestamp = image.m().get("timestamp").s();
                    var images = image.m().get("images").l();
                    return new ImageDTO(imagesCollectionId, timestamp, toImagesDTO(images),  item.get("imagesCollection").l().size());
                })).findAny().orElseThrow(() -> new RuntimeException("No images found"));
    }

    private ImageDTO.Image[] toImagesDTO(List<AttributeValue> images) {
        return images.stream().map(image1 -> {
            var imageId = image1.m().get("imageId").s();
            var url = image1.m().get("url").s();
            return new ImageDTO.Image(imageId, convertURLToCorrectFormat(url));
        }).toArray(ImageDTO.Image[]::new);
    }

    public static String convertURLToCorrectFormat(String urlToBeConverted) {
        return urlToBeConverted.replace("https://s3.amazonaws.com/chainbot.chaincuet.com.storage", "https://storage-chainbot.chaincuet.com");
    }
}
