package com.example.onlinemall;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String username;
    private String email;
    private String password;
    private int points; // 积分
    private int vipLevel; // VIP等级
    private List<Order> orders; // 订单列表

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.points = 0;
        this.vipLevel = 0;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
        // 每100积分提升1VIP等级
        this.vipLevel = this.points / 100;
    }
}