package com.rigadev.nutricapps.model;

public class OrderFoodDetailModel {
    String foodOrderID, name, qty, calories, price;

    public OrderFoodDetailModel() {
    }


    public String getFoodOrderID() {
        return foodOrderID;
    }

    public void setFoodOrderID(String foodOrderID) {
        this.foodOrderID = foodOrderID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
