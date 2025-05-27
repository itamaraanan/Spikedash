package com.example.spikedash_singleplayer.Items;
public class StoreItem {
    private String Id;
    private String name;
    private String imageUrl;
    private int price;

    public StoreItem(String Id, String name, String imageUrl, int price) {
        this.Id = Id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public StoreItem() {}

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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

