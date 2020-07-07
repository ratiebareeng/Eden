package com.example.eden.model;

import com.example.eden.model.OrderEntity;

import java.util.List;

public class OrderRequest {
    private long orderRequestId;
    private String customerName;
    private String customerEmail;
    private String customerContact;
    private String customerAddress;
    private String orderRequestDate;
    private String orderRequestStatus;
    private List<OrderEntity> orderRequestItemsList;
    private double orderRequestTotal;

    public OrderRequest() {
        // required
    }

    public OrderRequest(long orderRequestId, String customerName, String customerEmail, String customerContact,
                        String customerAddress, String orderRequestDate,
                        List<OrderEntity> orderRequestItemsList, double orderRequestTotal) {
        this.orderRequestId = orderRequestId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerContact = customerContact;
        this.customerAddress = customerAddress;
        this.orderRequestDate = orderRequestDate;
        this.orderRequestStatus = "0";
        this.orderRequestItemsList = orderRequestItemsList;
        this.orderRequestTotal = orderRequestTotal;
    }

    public long getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(long orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getOrderRequestDate() {
        return orderRequestDate;
    }

    public void setOrderRequestDate(String orderRequestDate) {
        this.orderRequestDate = orderRequestDate;
    }

    public String getOrderRequestStatus() {
        return orderRequestStatus;
    }

    public void setOrderRequestStatus(String orderRequestStatus) {
        this.orderRequestStatus = orderRequestStatus;
    }

    public List<OrderEntity> getOrderRequestItemsList() {
        return orderRequestItemsList;
    }

    public void setOrderRequestItemsList(List<OrderEntity> orderRequestItemsList) {
        this.orderRequestItemsList = orderRequestItemsList;
    }

    public double getOrderRequestTotal() {
        return orderRequestTotal;
    }

    public void setOrderRequestTotal(double orderRequestTotal) {
        this.orderRequestTotal = orderRequestTotal;
    }
}
