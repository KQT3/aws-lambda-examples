package org.example.dto;

import lombok.Value;

@Value
public class ImageDTO {
    String imagesCollectionId;
    String timestamp;
    Image[] images;

    @Value
    public static class Image {
        String imageId;
        String url;
    }

}

