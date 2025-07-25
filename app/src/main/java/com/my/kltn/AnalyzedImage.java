package com.my.kltn;

public class AnalyzedImage {
    private String imageUri;
    private String message;
    private String timestamp;

    public AnalyzedImage() {}

    public AnalyzedImage(String imageUri, String message, String timestamp) {
        this.imageUri = imageUri;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
