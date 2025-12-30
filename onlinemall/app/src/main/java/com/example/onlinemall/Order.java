package com.example.onlinemall;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private Date orderDate;
    private double totalAmount;
    private List<CartItem> items;
    private int earnedPoints; // 本次订单获得的积分

    public Order(String orderId, Date orderDate, double totalAmount, List<CartItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
        // 每消费100元获得1积分
        this.earnedPoints = (int) (totalAmount / 100);
    }

    // Getter方法
    public String getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }
}
