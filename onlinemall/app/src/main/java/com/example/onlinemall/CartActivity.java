package com.example.onlinemall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;


public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView textViewTotalPrice;
    private Button buttonCheckout;
    private CartManager cartManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance(this);

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        updateTotalPrice();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCart);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        buttonCheckout.setOnClickListener(v -> {
            if (cartManager.getCartItemCount() > 0) {
                // 跳转到支付页面，传递总金额
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("TOTAL_AMOUNT", cartManager.getTotalPrice());
                startActivity(intent);

                // 可选：清空购物车（或者等到支付成功后再清空）
                // cartManager.clearCart();
            } else {
                Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            }
        });

        /*buttonCheckout.setOnClickListener(v -> {
            if (cartManager.getCartItemCount() > 0) {
                // 模拟结算过程
                Toast.makeText(this, "结算成功！总金额: ¥" + cartManager.getTotalPrice(), Toast.LENGTH_SHORT).show();
                cartManager.clearCart();
                finish();
            } else {
                Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void setupRecyclerView() {
        List<CartItem> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, ProductListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                // 已在购物车页
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                //Toast.makeText(this, "个人中心功能开发中", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            return false;
        });

        // 设置选中状态
        //bottomNavigationView.setSelectedItemId(R.id.nav_cart);
    }

    private void updateTotalPrice() {
        textViewTotalPrice.setText(String.format("总计: ¥%.2f", cartManager.getTotalPrice()));
    }

    private void updateCartBadge() {
        if (bottomNavigationView != null) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(cartManager.getCartItemCount() > 0);
            badge.setNumber(cartManager.getCartItemCount());

            // 可选：自定义角标样式
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            badge.setBadgeTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    @Override
    public void onQuantityChanged(Product product, int quantity) {
        cartManager.updateCartItem(product, quantity);
        cartAdapter.updateCartItems(cartManager.getCartItems());
        updateTotalPrice();
        updateCartBadge();

    }

    @Override
    public void onItemRemoved(Product product) {
        cartManager.removeFromCart(product);
        cartAdapter.updateCartItems(cartManager.getCartItems());
        updateTotalPrice();
        updateCartBadge();
        if (cartManager.getCartItemCount() == 0) {
            Toast.makeText(this, "购物车已空", Toast.LENGTH_SHORT).show();
    }
    }
}
