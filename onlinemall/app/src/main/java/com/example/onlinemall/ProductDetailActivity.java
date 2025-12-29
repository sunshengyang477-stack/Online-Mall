package com.example.onlinemall;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageViewProduct;
    private TextView textViewProductName, textViewProductPrice, textViewProductDescription;
    private Button buttonAddToCart;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        product = (Product) getIntent().getSerializableExtra("product");

        if (product == null) {
            finish();
            return;
        }

        displayProductDetails(product);
        setupAddToCartButton();
    }

    private void initViews() {
        imageViewProduct = findViewById(R.id.imageViewProductDetail);
        textViewProductName = findViewById(R.id.textViewProductNameDetail);
        textViewProductPrice = findViewById(R.id.textViewProductPriceDetail);
        textViewProductDescription = findViewById(R.id.textViewProductDescriptionDetail);
        buttonAddToCart = findViewById(R.id.buttonAddToCartDetail);
    }

    private void displayProductDetails(Product product) {
        textViewProductName.setText(product.getName());
        textViewProductPrice.setText(String.format("¥%.2f", product.getPrice()));
        textViewProductDescription.setText(product.getDescription());

        // 关键修改：智能加载图片（兼容本地资源和网络URL）
        loadProductImage(product.getImageUrl());
    }

    private void loadProductImage(String imageUrl) {
        try {
            // 尝试解析为资源ID（适用于通过String.valueOf(R.drawable.xxx)传递的情况）
            int resId = Integer.parseInt(imageUrl);
            Glide.with(this)
                    .load(resId)
                    .placeholder(R.drawable.placeholder_product)
                    .into(imageViewProduct);
        } catch (NumberFormatException e) {
            // 如果不是数字，尝试作为资源名称加载
            int resId = getResources().getIdentifier(
                    imageUrl,  // 图片名称（如"smartphone"）
                    "drawable", // 资源类型
                    getPackageName()
            );

            if (resId != 0) {
                // 本地资源存在
                Glide.with(this)
                        .load(resId)
                        .placeholder(R.drawable.placeholder_product)
                        .into(imageViewProduct);
            } else {
                // 当作网络URL处理
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_product)
                        .into(imageViewProduct);
            }
        }
    }

    private void setupAddToCartButton() {
        buttonAddToCart.setOnClickListener(v -> {
            CartManager.getInstance(this).addToCart(product);
            Toast.makeText(this, "已添加 " + product.getName() + " 到购物车", Toast.LENGTH_SHORT).show();
        });
    }
}