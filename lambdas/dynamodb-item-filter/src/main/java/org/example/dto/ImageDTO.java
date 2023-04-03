package org.example.dto;

import lombok.Value;

@Value
public class ImageDTO {
    String imagesCollectionId;
    String timestamp;
    Image[] images;
    int totalImages;

    @Value
    public static class Image {
        String imageId;
        String url;
    }

}

