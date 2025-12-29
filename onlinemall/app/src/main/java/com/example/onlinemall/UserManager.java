package com.example.onlinemall;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserManager {
    private static final String USER_PREFS = "UserPrefs";
    private static final String CURRENT_USER_KEY = "currentUser";
    private static final String ORDERS_KEY = "userOrders";
    private static UserManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private User currentUser;
    private List<Order> userOrders;

    private UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCurrentUser();
        loadUserOrders();

        // 如果没有订单数据，创建一些测试订单
        if (userOrders == null || userOrders.isEmpty()) {
            userOrders = new ArrayList<>();
            createTestOrders();
            saveUserOrders();

            if (currentUser != null) {
                currentUser.setOrders(userOrders);
                saveCurrentUser();
            }
        }
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void login(User user) {
        this.currentUser = user;
        saveCurrentUser();
    }

    public void logout() {
        this.currentUser = null;
        sharedPreferences.edit().remove(CURRENT_USER_KEY).apply();
    }

    public void addOrder(double totalAmount, List<CartItem> items) {
        String orderId = UUID.randomUUID().toString();
        Order newOrder = new Order(orderId, new Date(), totalAmount, items);

        // 添加积分
        currentUser.addPoints(newOrder.getEarnedPoints());

        userOrders.add(0, newOrder); // 添加到列表开头
        currentUser.setOrders(userOrders);

        saveCurrentUser();
        saveUserOrders();
    }

    public List<Order> getUserOrders() {
        return userOrders;
    }

    private void loadCurrentUser() {
        String json = sharedPreferences.getString(CURRENT_USER_KEY, null);
        if (json != null) {
            currentUser = gson.fromJson(json, User.class);
        }
    }

    private void saveCurrentUser() {
        if (currentUser != null) {
            String json = gson.toJson(currentUser);
            sharedPreferences.edit().putString(CURRENT_USER_KEY, json).apply();
        }
    }

    private void loadUserOrders() {
        String json = sharedPreferences.getString(ORDERS_KEY, null);
        Type type = new TypeToken<ArrayList<Order>>() {}.getType();
        userOrders = gson.fromJson(json, type);
        if (userOrders == null) {
            userOrders = new ArrayList<>();
        }

        if (currentUser != null) {
            currentUser.setOrders(userOrders);
        }
    }

    private void saveUserOrders() {
        String json = gson.toJson(userOrders);
        sharedPreferences.edit().putString(ORDERS_KEY, json).apply();
    }

    private void createTestOrders() {
        // 创建一些测试商品
        Product product1 = new Product("1", "智能手机", 2999.00, "最新款智能手机，高性能处理器", "https://example.com/phone.jpg");
        Product product2 = new Product("2", "无线耳机", 399.00, "降噪无线蓝牙耳机", "https://example.com/earphone.jpg");
        Product product3 = new Product("3", "智能手表", 899.00, "健康监测，运动追踪", "https://example.com/watch.jpg");
        Product product4 = new Product("4", "笔记本电脑", 5999.00, "轻薄本，长续航", "https://example.com/laptop.jpg");

        // 订单1 - 3天前 (总价: 2999 + 399*2 = 3797 → 37积分)
        List<CartItem> items1 = new ArrayList<>();
        items1.add(new CartItem(product1, 1));
        items1.add(new CartItem(product2, 2));
        userOrders.add(new Order(
                generateOrderId(),
                new Date(System.currentTimeMillis() - 86400000 * 3),
                3797.00,
                items1
        ));

        // 订单2 - 2天前 (总价: 899 → 8积分)
        List<CartItem> items2 = new ArrayList<>();
        items2.add(new CartItem(product3, 1));
        userOrders.add(new Order(
                generateOrderId(),
                new Date(System.currentTimeMillis() - 86400000 * 2),
                899.00,
                items2
        ));

        // 订单3 - 1天前 (总价: 5999 + 399 = 6398 → 63积分)
        List<CartItem> items3 = new ArrayList<>();
        items3.add(new CartItem(product4, 1));
        items3.add(new CartItem(product2, 1));
        userOrders.add(new Order(
                generateOrderId(),
                new Date(System.currentTimeMillis() - 86400000),
                6398.00,
                items3
        ));

        // 订单4 - 今天 (总价: 2999 + 899*2 = 4797 → 47积分)
        List<CartItem> items4 = new ArrayList<>();
        items4.add(new CartItem(product1, 1));
        items4.add(new CartItem(product3, 2));
        userOrders.add(new Order(
                generateOrderId(),
                new Date(),
                4797.00,
                items4
        ));

        // 计算总积分 (37 + 8 + 63 + 47 = 155积分 → VIP 1级)
        if (currentUser != null) {
            currentUser.setPoints(155);
            currentUser.setVipLevel(1);
        }
    }

    private String generateOrderId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}