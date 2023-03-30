package org.example;

public class EventBody {
    private String subId;
    private String imagesCollectionId;
    private String imageIndex;

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public void setImagesCollectionId(String imagesCollectionId) {
        this.imagesCollectionId = imagesCollectionId;
    }

    public void setImageIndex(String imageIndex) {
        this.imageIndex = imageIndex;
    }

    public String getSubId() {
        return subId;
    }

    public String getImagesCollectionId() {
        return imagesCollectionId;
    }

    public String getImageIndex() {
        return imageIndex;
    }
}
