package com.example.spikedash_singleplayer;
public class Skin {
    private String skinId;
    private String name;
    private String imageUrl;
    private int price;

    public Skin(String skinId, String name, String imageUrl, int price) {
        this.skinId = skinId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public Skin() {}

    public String getSkinId() {
        return skinId;
    }

    public void setSkinId(String skinId) {
        this.skinId = skinId;
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

