package com.example.spikedash_singleplayer;

import java.util.HashMap;
import java.util.Map;

public class Background {
    private String id;
    private String name;
    private int price;
    private int resourceId;
    private boolean isUnlocked;

    public Background() {}

    public Background(String id, String name, int price, int resourceId, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.resourceId = resourceId;
        this.isUnlocked = isUnlocked;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("price", price);
        result.put("resourceId", resourceId);
        result.put("unlocked", isUnlocked);
        return result;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}