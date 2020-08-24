package com.example.eden.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class Combo {
    private String comboName;
    private double comboPrice;
    private ArrayList<String> comboItems;
    private String iUrl;

    public Combo() {
        // firebase required
    }

    @PropertyName("name")
    public String getComboName() {
        return comboName;
    }
    @PropertyName("name")
    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    @PropertyName("price")
    public double getComboPrice() {
        return comboPrice;
    }
    @PropertyName("price")
    public void setComboPrice(double comboPrice) {
        this.comboPrice = comboPrice;
    }

    public String getiUrl() {
        return iUrl;
    }

    public void setiUrl(String iUrl) {
        this.iUrl = iUrl;
    }

    @Exclude
    @PropertyName("itemList")
    public ArrayList<String> getComboItems() {
        return comboItems;
    }
    @Exclude
    @PropertyName("itemList")
    public void setComboItems(ArrayList<String> comboItems) {
        this.comboItems = comboItems;
    }
}
