package com.example.appmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmoney.app.api.RetrofitClient;
import com.example.appmoney.app.model.LoginRequest;
import com.example.appmoney.app.model.LoginResponse;
import com.example.appmoney.databinding.ActivityLoginBinding;
import com.example.appmoney.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        // Đã đăng nhập → vào thẳng app
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> doLogin());

        binding.tvForgot.setOnClickListener(v ->
                Toast.makeText(this,
                        "Liên hệ quản trị viên để lấy lại mật khẩu",
                        Toast.LENGTH_LONG).show());

        binding.tvRegister.setOnClickListener(v ->
                Toast.makeText(this,
                        "Liên hệ quản trị viên để tạo tài khoản",
                        Toast.LENGTH_SHORT).show());
    }

    private void doLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập tên đăng nhập");
            binding.etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Vui lòng nhập mật khẩu");
            binding.etPassword.requestFocus();
            return;
        }

        setLoading(true);
        hideError();

        // ✅ Reset service để dùng cookie mới từ lần login này
        RetrofitClient.resetService();

        LoginRequest request = new LoginRequest(username, password);

        RetrofitClient.getService()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();

                            if (body.isSuccess() && body.isLoggedIn()) {
                                // ✅ Truyền password vào để dùng cho auto-login sau này
                                handleLoginSuccess(body, password);
                            } else {
                                String msg = body.getMessage();
                                showError(msg != null && !msg.isEmpty()
                                        ? msg
                                        : "Sai tên đăng nhập hoặc mật khẩu!");
                            }

                        } else {
                            switch (response.code()) {
                                case 400: showError("Thông tin không hợp lệ"); break;
                                case 401: showError("Sai tên đăng nhập hoặc mật khẩu"); break;
                                case 403: showError("Tài khoản bị khoá"); break;
                                case 500: showError("Lỗi server, thử lại sau"); break;
                                default:  showError("Lỗi kết nối: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        showError("Không kết nối được server!\nKiểm tra lại kết nối mạng.");
                    }
                });
    }

    private void handleLoginSuccess(LoginResponse body, String password) {
        String tokenKey  = body.getUserTokenKey();
        String loginName = body.getLoginName();

        LoginResponse.CurrentUser user = body.getCurrentUser();
        String fullName = loginName;
        String email    = "";
        int    userId   = 0;
        boolean isAdmin = false;

        if (user != null) {
            if (user.getName() != null && !user.getName().isEmpty()) fullName = user.getName();
            if (user.getEmail() != null) email = user.getEmail();
            userId  = user.getId();
            isAdmin = user.isSiteAdmin();
        }

        // ✅ Lưu cả password để auto-login khi app khởi động lại
        session.saveLoginData(tokenKey, loginName, fullName, email, userId, isAdmin, password);

        Toast.makeText(this, "Chào mừng " + fullName + "! 👋", Toast.LENGTH_SHORT).show();
        goToMain();
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnLogin.setText(loading ? "Đang đăng nhập..." : "Đăng nhập");
    }

    private void showError(String msg) {
        binding.tvError.setText("❌  " + msg);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }
}