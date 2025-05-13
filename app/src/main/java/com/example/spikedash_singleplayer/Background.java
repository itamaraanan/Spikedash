package com.example.spikedash_singleplayer;
public class Background {
    private String backgroundId;
    private String name;
    private String imageUrl;
    private int price;

    public Background(String backgroundId, String name, String imageUrl, int price) {
        this.backgroundId = backgroundId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public Background() {}

    public String getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(String backgroundId) {
        this.backgroundId = backgroundId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

