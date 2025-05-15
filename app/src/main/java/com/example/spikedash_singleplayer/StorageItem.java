package com.example.spikedash_singleplayer;
// StorageItem.java
public class StorageItem {
    private String id;    // Firestore document ID (e.g., "ocean_background")
    private String name;  // Display name (e.g., "Ocean background")
    private String imageUrl;

    public StorageItem() {} // Required for Firebase

    public StorageItem(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
