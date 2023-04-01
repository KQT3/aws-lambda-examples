import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.Handler;
import org.example.ImagesCollection;
import org.example.JWTHelper;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

@Slf4j
@Disabled
class HandlerTest {
    Context context = new TestContext();
    Handler handler = mock(Handler.class);

    final String AUTHORIZATION_HEADER = "authorization";

    @Test
    public void testHandleRequest() throws NoSuchAlgorithmException {
        //given
        String ACCESS_TOKEN = JWTHelper.createTestToken();
        Mockito.when(handler.getItemFiltered(anyString(), anyString(), anyString())).thenReturn(null);
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
    void testSerialization() throws Exception {
        // Create a sample ImagesCollection object
        List<Map<String, ImagesCollection.Images>> list = new ArrayList<>();
        Map<String, ImagesCollection.Images> map = new HashMap<>();
        List<ImagesCollection.Images.Image> imagesList = new ArrayList<>();
        Map<String, ImagesCollection.Images.ImageObject> imageMap = new HashMap<>();
        imageMap.put("imageId", new ImagesCollection.Images.ImageObject("030cb329-023e-4d26-9c54-3f00fa6d0662"));
        imageMap.put("url", new ImagesCollection.Images.ImageObject("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/"));
        ImagesCollection.Images.Image image = new ImagesCollection.Images.Image(imageMap);
        imagesList.add(image);
        ImagesCollection.Images images = new ImagesCollection.Images(imagesList, new ImagesCollection.Images.ImageObject("53ceeda8-e6fe-4f53-ab65-c8e0b1de5dbf"), new ImagesCollection.Images.ImageObject("2023-03-25T14:04:49.012Z"));
        map.put("imagesCollection", images);
        list.add(map);
        ImagesCollection imagesCollection = new ImagesCollection(list);

        // Serialize the ImagesCollection object
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(imagesCollection);
        // Check if the serialized JSON string matches the expected output
        String expectedJson = "{\"l\":[{\"imagesCollection\":{\"images\":[{\"m\":{\"imageId\":{\"s\":\"030cb329-023e-4d26-9c54-3f00fa6d0662\"},\"url\":{\"s\":\"https://s3.amazonaws.com/chainbot.chaincuet.com.storage/\"}}}],\"imagesCollectionId\":{\"s\":\"53ceeda8-e6fe-4f53-ab65-c8e0b1de5dbf\"},\"timestamp\":{\"s\":\"2023-03-25T14:04:49.012Z\"}}}]}";
        System.out.println(json);
//        objectMapper.writ
        assertEquals(expectedJson, json);
    }

    @Test
    void name() {
        List<Map<String, ImagesCollection.Images>> imagesCollectionL = new ArrayList<>();
//        List<ImagesCollection.Images.Image> images = new ArrayList<>();

        List<Map<String, AttributeValue>> items = new ArrayList<>();
        ImagesCollection.Images.ImageObject imageId = new ImagesCollection.Images.ImageObject(UUID.randomUUID().toString());
        ImagesCollection.Images.ImageObject url = new ImagesCollection.Images.ImageObject("https://s3.amazonaws.com/chainbot.chaincuet.com.storage");

        ImagesCollection.Images.ImageObject imagesCollectionId = new ImagesCollection.Images.ImageObject(UUID.randomUUID().toString());
        ImagesCollection.Images.ImageObject timestamp = new ImagesCollection.Images.ImageObject("date");

        List<ImagesCollection.Images.Image> images = new ArrayList<>();

        Map<String, ImagesCollection.Images.ImageObject> image = new HashMap<>();
        image.put("imageId", imageId);
        image.put("url", url);
        ImagesCollection.Images.Image imageM = new ImagesCollection.Images.Image(image);
        images.add(imageM);

        ImagesCollection.Images imageCollection = new ImagesCollection.Images(images, imagesCollectionId, timestamp);
        Map<String, ImagesCollection.Images> imagesCollectio = new HashMap<>();
        imagesCollectio.put("imagesCollection", imageCollection);

        imagesCollectionL.add(imagesCollectio);

        ImagesCollection imagesCollectionObject = new ImagesCollection(imagesCollectionL);
    }
}
