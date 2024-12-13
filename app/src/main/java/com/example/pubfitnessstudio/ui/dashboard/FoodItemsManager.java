package com.example.pubfitnessstudio.ui.dashboard;


import com.example.pubfitnessstudio.database.FoodItem;

import java.util.List;

public class FoodItemsManager {
    private static FoodItemsManager instance;
    private List<FoodItem> foodItems; // List to store food items

    private FoodItemsManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized FoodItemsManager getInstance() {
        if (instance == null) {
            instance = new FoodItemsManager();
        }
        return instance;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    // Clears food items if needed (e.g., for reloading)
    public void clearFoodItems() {
        this.foodItems = null;
    }
}
