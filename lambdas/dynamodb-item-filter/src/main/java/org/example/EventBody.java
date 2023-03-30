package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import lombok.Value;

public class EventBody {
    Body body;

    public EventBody() {
    }

    public EventBody(Body body) {
        this.body = body;
    }

    public static class Body {
        String subId;
        String imagesCollectionId;
        String imageIndex;

        public Body() {
        }

        public Body(String subId, String imagesCollectionId, String imageIndex) {
            this.subId = subId;
            this.imagesCollectionId = imagesCollectionId;
            this.imageIndex = imageIndex;
        }
    }

    public Body getBody() {
        return body;
    }
}
