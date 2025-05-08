package com.example.spikedash_singleplayer;

public class Skin {
    public String skinId;
    public String name;
    public String imageUrl;
    public int price;
    public boolean isUnlocked;

    public Skin(String skinId, String name, String imageUrl, int price, boolean isUnlocked) {
        this.skinId = skinId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isUnlocked = isUnlocked;
    }
    public Skin() {}
    public String getSkinId() {
        return skinId;
    }
    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public int getPrice() {
        return price;
    }
    public boolean isUnlocked() {
        return isUnlocked;
    }
    public void setSkinId(String skinId) {
        this.skinId = skinId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setPrice(int price) {
        this.price = price;
    }

}
