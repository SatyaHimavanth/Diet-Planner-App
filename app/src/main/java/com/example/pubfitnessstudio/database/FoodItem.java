package com.example.pubfitnessstudio.database;

import java.text.DecimalFormat;

public class FoodItem {

    private String name;
    private double calories;
    private double carbs;
    private double proteins;
    private double fats;

    public FoodItem(String name, double calories, double carbs, double proteins, double fats) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fats = fats;
    }

    public String getName() {return name;}

    public double getCalories() {
        return Math.round(calories * 100.0) / 100.0;
    }

    public double getCarbs() {return Math.round(carbs * 100.0) / 100.0;}

    public double getProteins(){return Math.round(proteins * 100.0) / 100.0;}

    public double getFats(){return Math.round(fats * 100.0) / 100.0;}
}
