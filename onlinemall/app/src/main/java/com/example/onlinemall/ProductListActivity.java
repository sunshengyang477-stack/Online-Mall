package com.example.onlinemall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // 初始化视图
        initViews();

        // 设置商品列表
        setupProductList();

        // 设置下拉刷新
        setupSwipeRefresh();

        // 设置底部导航
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupProductList() {
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        productAdapter = new ProductAdapter(getSampleProducts(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                addToCart(product);
            }
        });

        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                productAdapter.updateProducts(getSampleProducts());
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ProductListActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }, 1500);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 已经是首页
                //startActivity(new Intent(this, ProductListActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                // 临时处理 - 显示提示信息
                //Toast.makeText(this, "购物车功能开发中", Toast.LENGTH_SHORT).show();
                // 正式使用时替换为:
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                //Toast.makeText(this, "个人中心功能开发中", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void addToCart(Product product) {
        // 使用CartManager添加商品到购物车
        CartManager.getInstance(this).addToCart(product);
        Toast.makeText(this, "已添加 " + product.getName() + " 到购物车", Toast.LENGTH_SHORT).show();

        // 可以在这里更新购物车图标上的数量（如果有）
        updateCartBadge();
    }
    private void updateCartBadge() {
        // 如果有底部导航栏的购物车图标，可以更新角标
        int cartItemCount = CartManager.getInstance(this).getCartItemCount();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(cartItemCount > 0);
            badge.setNumber(cartItemCount);
        }
    }

    private List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("1", "智能手机", 2999.00, "最新款智能手机，高性能处理器", String.valueOf(R.drawable.smartphone)));
        products.add(new Product("2", "无线耳机", 399.00, "降噪无线蓝牙耳机", String.valueOf(R.drawable.earphone)));
        products.add(new Product("3", "智能手表", 899.00, "健康监测，运动追踪", String.valueOf(R.drawable.smartwatch)));
        products.add(new Product("4", "笔记本电脑", 5999.00, "轻薄本，长续航", String.valueOf(R.drawable.laptop)));
        products.add(new Product("5", "平板电脑", 2599.00, "大屏幕，适合学习和娱乐", String.valueOf(R.drawable.tablet)));
        return products;
    }
}