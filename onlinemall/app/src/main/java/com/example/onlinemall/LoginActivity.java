package com.example.onlinemall;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // 登录相关视图
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSwitchToRegister;
    private LinearLayout loginForm;

    // 注册相关视图
    private TextInputEditText etRegUsername, etRegEmail, etRegPassword, etRegConfirmPassword;
    private Button btnRegister;
    private TextView tvSwitchToLogin;
    private LinearLayout registerForm;

    // 通用视图
    private ProgressBar progressBar;
    private TextView tvTitle;

    // 数据库帮助类
    private UserDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化数据库
        dbHelper = new UserDbHelper(this);

        // 初始化视图
        initViews();

        // 设置点击监听器
        setupClickListeners();

        // 检查是否已登录（可选）
        //checkAlreadyLoggedIn();
    }

    private void initViews() {
        // 登录相关视图
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSwitchToRegister = findViewById(R.id.tvSwitchToRegister);
        loginForm = findViewById(R.id.loginForm);

        // 注册相关视图
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvSwitchToLogin = findViewById(R.id.tvSwitchToLogin);
        registerForm = findViewById(R.id.registerForm);

        // 通用视图
        progressBar = findViewById(R.id.progressBar);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void setupClickListeners() {
        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLoginInput(username, password)) {
                performLogin(username, password);
            }
        });

        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPassword = etRegConfirmPassword.getText().toString().trim();

            if (validateRegisterInput(username, email, password, confirmPassword)) {
                performRegister(username, email, password);
            }
        });

        // 切换到注册表单
        tvSwitchToRegister.setOnClickListener(v -> {
            tvTitle.setText("用户注册");
            loginForm.setVisibility(View.GONE);
            registerForm.setVisibility(View.VISIBLE);
            clearRegisterForm();
        });

        // 切换到登录表单
        tvSwitchToLogin.setOnClickListener(v -> {
            tvTitle.setText("用户登录");
            registerForm.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
            clearLoginForm();
        });
    }

    private boolean validateLoginInput(String username, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            valid = false;
        }

        return valid;
    }

    private boolean validateRegisterInput(String username, String email, String password, String confirmPassword) {
        boolean valid = true;

        if (TextUtils.isEmpty(username)) {
            etRegUsername.setError("请输入用户名");
            valid = false;
        } else if (username.length() < 4) {
            etRegUsername.setError("用户名至少4个字符");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("请输入邮箱");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("请输入有效的邮箱地址");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("请输入密码");
            valid = false;
        } else if (password.length() < 6) {
            etRegPassword.setError("密码长度至少6位");
            valid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etRegConfirmPassword.setError("请确认密码");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            etRegConfirmPassword.setError("两次输入的密码不一致");
            valid = false;
        }

        return valid;
    }


    private void performLogin(String username, String password) {
        showLoading(true);

        // 模拟网络请求延迟
        new android.os.Handler().postDelayed(() -> {
            showLoading(false);

            // 检查用户是否存在且密码正确
            if (dbHelper.checkUser(username, password)) {
                // 登录成功
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

                // 保存登录状态（可选）
                saveLoginStatus(username);

                // 跳转到商品列表页面
                Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                startActivity(intent);
                finish(); // 关闭登录页面，防止返回
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private void performRegister(String username, String email, String password) {
        showLoading(true);

        // 模拟网络请求延迟
        new android.os.Handler().postDelayed(() -> {
            showLoading(false);

            // 检查用户名是否已存在
            if (dbHelper.isUsernameExists(username)) {
                etRegUsername.setError("用户名已存在");
                return;
            }

            // 检查邮箱是否已存在
            if (dbHelper.isEmailExists(email)) {
                etRegEmail.setError("邮箱已被注册");
                return;
            }

            // 添加用户到数据库
            long id = dbHelper.addUser(username, email, password);
            if (id != -1) {
                Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();

                // 切换到登录表单并填充用户名
                switchToLoginForm();
                etUsername.setText(username);
                etPassword.setText("");
            } else {
                Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
        }
    }

    private void switchToLoginForm() {
        tvTitle.setText("用户登录");
        registerForm.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
    }

    private void clearLoginForm() {
        etUsername.setText("");
        etPassword.setText("");
        etUsername.setError(null);
        etPassword.setError(null);
    }

    private void clearRegisterForm() {
        etRegUsername.setText("");
        etRegEmail.setText("");
        etRegPassword.setText("");
        etRegConfirmPassword.setText("");
        etRegUsername.setError(null);
        etRegEmail.setError(null);
        etRegPassword.setError(null);
        etRegConfirmPassword.setError(null);
    }

    private void saveLoginStatus(String username) {
        // 使用SharedPreferences保存登录状态
        getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", true)
                .putString("username", username)
                .apply();
    }

    private void checkAlreadyLoggedIn() {
        // 检查用户是否已登录
        boolean isLoggedIn = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(this, ProductListActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}