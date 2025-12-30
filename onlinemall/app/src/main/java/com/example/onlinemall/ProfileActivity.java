package com.example.onlinemall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private TextView textViewEmail;
    private TextView textViewPoints;
    private TextView textViewVipLevel;
    private RecyclerView recyclerViewOrders;
    private Button buttonLogout;
    private OrderAdapter orderAdapter;
    private UserManager userManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = UserManager.getInstance(this);

        initViews();
        setupUserInfo();
        setupOrderHistory();
        setupLogoutButton();
        setupBottomNavigation();
    }

    private void initViews() {
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPoints = findViewById(R.id.textViewPoints);
        textViewVipLevel = findViewById(R.id.textViewVipLevel);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        buttonLogout = findViewById(R.id.buttonLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupUserInfo() {
        User user = userManager.getCurrentUser();
        if (user != null) {
            textViewUsername.setText(user.getUsername());
            textViewEmail.setText(user.getEmail());
            textViewPoints.setText(String.format("积分: %d", user.getPoints()));
            textViewVipLevel.setText(String.format("VIP等级: %d", user.getVipLevel()));
        }
    }

    private void setupOrderHistory() {
        List<Order> orders = userManager.getUserOrders();
        //List<Order> orders = getSampleOrders();
        orderAdapter = new OrderAdapter(orders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);
    }



    private void setupLogoutButton() {
        buttonLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // 返回上一页或跳转到登录页
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, ProductListActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

}
