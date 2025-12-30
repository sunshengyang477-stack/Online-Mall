package com.example.onlinemall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onlinemall.Product;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private Button buttonCompletePayment;
    private TextView textViewTotalAmount;
    private ImageView imageViewQRCode;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 从Intent获取总金额
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.00);

        initViews();
        setupListeners();
        loadQRCode();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        buttonCompletePayment = findViewById(R.id.buttonCompletePayment);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);

        // 显示总金额
        textViewTotalAmount.setText(String.format("¥ %.2f", totalAmount));
    }

    private void setupListeners() {
        // 返回按钮点击事件
        buttonBack.setOnClickListener(v -> {
            // 返回到购物车页面
            onBackPressed();
        });

        // 支付完成按钮点击事件
        buttonCompletePayment.setOnClickListener(v -> {
            // 模拟支付处理过程
            showPaymentProcessing();
        });
    }

    private void loadQRCode() {
        // 这里可以加载真实的支付二维码图片
        // 示例：使用Glide加载网络图片或本地资源
        // Glide.with(this).load("https://example.com/qrcode.png").into(imageViewQRCode);

        // 暂时使用本地占位图
        imageViewQRCode.setImageResource(R.drawable.ic_qr_code_placeholder);
    }

    private void showPaymentProcessing() {
        // 禁用按钮，防止重复点击
        buttonCompletePayment.setEnabled(false);
        buttonCompletePayment.setText("支付处理中...");

        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            // 支付成功
            showPaymentSuccess();
        }, 2000);
    }

    private void showPaymentSuccess() {
        // 显示支付成功消息
        Toast.makeText(this, "支付成功！感谢您的购买", Toast.LENGTH_LONG).show();

        // 创建订单成功通知
        createOrderNotification();

        // 延迟后返回主页
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PaymentActivity.this, ProductListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("SHOW_SUCCESS_MESSAGE", true);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void createOrderNotification() {
        // 这里可以添加创建订单的逻辑
        // 例如：保存订单到数据库、发送订单确认邮件等

        // 清除购物车（从CartActivity传过来的数据）
        CartManager.getInstance(this).clearCart();

        // 可以在这里发送广播通知其他页面更新
        Intent intent = new Intent("PAYMENT_SUCCESS");
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        // 返回时询问用户是否确认取消支付
        showBackConfirmationDialog();
    }

    private void showBackConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("确认离开")
                .setMessage("您尚未完成支付，确定要离开支付页面吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 返回到购物车页面
                    Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
    }
}