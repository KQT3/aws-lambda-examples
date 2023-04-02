import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.Handler;
import org.example.JWTHelper;
import org.example.dto.ImageDTO;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.example.Handler.convertURLToCorrectFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

@Slf4j
@Disabled
class HandlerTest {
    Context context = new TestContext();
    Handler handler = mock(Handler.class);
    QueryResponse queryResponse = mock(QueryResponse.class);
    final String AUTHORIZATION_HEADER = "authorization";

    @Test
    public void testHandleRequest() throws NoSuchAlgorithmException {
        //given
        String ACCESS_TOKEN = JWTHelper.createTestToken();
        Mockito.when(handler.getItemFiltered(anyString(), anyString(), anyString())).thenReturn(queryResponse);
        Mockito.when(queryResponse.items()).thenReturn(null);
        Mockito.when(handler.handleRequest(any(), any())).thenCallRealMethod();

        Map<String, String> eventBody = new HashMap<>();
        eventBody.put("imagesCollectionId", "0");
        eventBody.put("imageIndex", "5ec5d9a2-3104-4472-89ad-fc45bf4ade51");
        String body = new JSONObject(eventBody).toString();

        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "Bearer " + ACCESS_TOKEN);
        headers.put("Content-Type", "application/json");

        APIGatewayV2HTTPResponse request = APIGatewayV2HTTPResponse.builder()
                .withHeaders(headers)
                .withBody(body)
                .build();

        // when
        var result = handler.handleRequest(request, context);

        System.out.println(result);
    }

    @Test
    public void testConvertDynamoDBItemToDTO() {
        //given
        Mockito.when(handler.toDTO(any())).thenCallRealMethod();

        AttributeValue imageId1 = AttributeValue.fromS("030cb329-023e-4d26-9c54-3f00fa6d0662");
        AttributeValue url1 = AttributeValue.fromS("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42");
        Map<String, AttributeValue> image1 = Map.of("imageId", imageId1, "url", url1);

        AttributeValue imageId2 = AttributeValue.fromS("792b0ec0-f49e-475a-99a9-0eb4d7ee38bf");
        AttributeValue url2 = AttributeValue.fromS("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42");
        Map<String, AttributeValue> image2 = Map.of("imageId", imageId2, "url", url2);

        AttributeValue imageId3 = AttributeValue.fromS("6f13b587-256a-49f3-a6c3-e799d4b8d605");
        AttributeValue url3 = AttributeValue.fromS("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42");
        Map<String, AttributeValue> image3 = Map.of("imageId", imageId3, "url", url3);

        AttributeValue imageId4 = AttributeValue.fromS("b0fbf557-65a7-4f65-af74-870026b2b8f9");
        AttributeValue url4 = AttributeValue.fromS("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42");
        Map<String, AttributeValue> image4 = Map.of("imageId", imageId4, "url", url4);

        AttributeValue images = AttributeValue.fromL(List.of(
                AttributeValue.fromM(image1),
                AttributeValue.fromM(image2),
                AttributeValue.fromM(image3),
                AttributeValue.fromM(image4)
        ));

        AttributeValue imagesCollectionId = AttributeValue.fromS("53ceeda8-e6fe-4f53-ab65-c8e0b1de5dbf");
        AttributeValue timestamp = AttributeValue.fromS("2023-03-25T14:04:49.012Z");

        AttributeValue imagesCollection = AttributeValue.fromM(Map.of(
                "images", images,
                "imagesCollectionId", imagesCollectionId,
                "timestamp", timestamp
        ));

        AttributeValue item = AttributeValue.fromL(List.of(imagesCollection));

        List<Map<String, AttributeValue>> items = List.of(Map.of("imagesCollection", item));

        //when
        var dto = handler.toDTO(items);

        //then
        assertEquals(timestamp.s(), dto.getTimestamp());
        assertEquals(imagesCollectionId.s(), dto.getImagesCollectionId());
        assertEquals(4, dto.getImages().length);
        assertEquals(image1.get("imageId").s(), dto.getImages()[0].getImageId());
        assertEquals(convertURLToCorrectFormat(image1.get("url").s()), dto.getImages()[0].getUrl());
        assertEquals(image2.get("imageId").s(), dto.getImages()[1].getImageId());
        assertEquals(convertURLToCorrectFormat(image2.get("url").s()), dto.getImages()[1].getUrl());
        assertEquals(image3.get("imageId").s(), dto.getImages()[2].getImageId());
        assertEquals(convertURLToCorrectFormat(image3.get("url").s()), dto.getImages()[2].getUrl());
        assertEquals(image4.get("imageId").s(), dto.getImages()[3].getImageId());
        assertEquals(convertURLToCorrectFormat(image4.get("url").s()), dto.getImages()[3].getUrl());
    }

    @Test
    void convertURLToCorrectFormatSuccess() {
        //given
        String urlToBeConverted = "https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42";
        String expectedUrl = "https://storage-chainbot.chaincuet.com/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42";

        //when
        String result = convertURLToCorrectFormat(urlToBeConverted);

        //then
        assertEquals(expectedUrl, result);
    }
}
