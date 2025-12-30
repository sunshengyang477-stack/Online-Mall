package com.example.onlinemall;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_ITEMS_KEY = "cartItems";
    private static CartManager instance;
    private SharedPreferences sharedPreferences;
    private List<CartItem> cartItems;

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        cartItems = getCartItemsFromPrefs();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void addToCart(Product product) {
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new CartItem(product, 1));
        }
        saveCartItemsToPrefs();
    }

    public void updateCartItem(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (quantity <= 0) {
                    cartItems.remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
        saveCartItemsToPrefs();
    }

    public void removeFromCart(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                cartItems.remove(item);
                break;
            }
        }
        saveCartItemsToPrefs();
    }

    public void clearCart() {
        cartItems.clear();
        saveCartItemsToPrefs();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    private void saveCartItemsToPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString(CART_ITEMS_KEY, json);
        editor.apply();
    }

    private List<CartItem> getCartItemsFromPrefs() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        List<CartItem> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }
}