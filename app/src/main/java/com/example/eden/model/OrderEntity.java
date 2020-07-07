package com.example.eden.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_table")
public class OrderEntity {
    @PrimaryKey(autoGenerate = true)
    private int orderId;
    private String itemId;
    private String itemName;
    private double itemPrice;
    private int itemQuantity;
    private double itemsTotalPrice;
    private String itemImageUrl;

    public OrderEntity() {
        //required
    }

    public OrderEntity(String itemName, double itemPrice, int itemQuantity, double itemsTotalPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.itemsTotalPrice = itemsTotalPrice;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemsTotalPrice() {
        return itemsTotalPrice;
    }

    public void setItemsTotalPrice(double itemsTotalPrice) {
        this.itemsTotalPrice = itemsTotalPrice;
    }

}
