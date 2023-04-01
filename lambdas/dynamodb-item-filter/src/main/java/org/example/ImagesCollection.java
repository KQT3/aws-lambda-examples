package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class ImagesCollection {
    List<Map<String, Images>> L;

    @JsonCreator
    public ImagesCollection(@JsonProperty("L") List<Map<String, Images>> L) {
        this.L = L;
    }

    @Getter
    public static class Images {
        List<Image> images;
        ImageObject imagesCollectionId;
        ImageObject timestamp;

        @JsonCreator
        public Images(@JsonProperty("images") List<Image> images,
                      @JsonProperty("imagesCollectionId") ImageObject imagesCollectionId,
                      @JsonProperty("timestamp") ImageObject timestamp) {
            this.images = images;
            this.imagesCollectionId = imagesCollectionId;
            this.timestamp = timestamp;
        }

        @Getter
        public static class Image {
            Map<String, ImageObject> M;

            @JsonCreator
            public Image(@JsonProperty("M") Map<String, ImageObject> M) {
                this.M = M;
            }
        }

        @Getter
        public static class ImageObject {
            String S;

            @JsonCreator
            public ImageObject(@JsonProperty("S") String S) {
                this.S = S;
            }
        }
    }
}

