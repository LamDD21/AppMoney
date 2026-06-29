package com.example.appmoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appmoney.app.api.RetrofitClient;
import com.example.appmoney.app.model.LoginRequest;
import com.example.appmoney.app.model.LoginResponse;
import com.example.appmoney.app.utils.SessionManager;
import com.example.appmoney.databinding.ActivityMainBinding;
import com.example.appmoney.ui.home.HomeFragment;
import com.example.appmoney.ui.profile.ProfileFragment;
import com.example.appmoney.ui.reports.ReportsFragment;
import com.example.appmoney.ui.transaction.AddTransactionActivity;
import com.example.appmoney.ui.transaction.TransactionsFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Auto-login ngầm để lấy cookie mới mỗi khi app khởi động
        autoLoginIfNeeded();

        // Hiện Home khi mở app
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;

            } else if (id == R.id.nav_transactions) {
                loadFragment(new TransactionsFragment());
                return true;

            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, AddTransactionActivity.class));
                return false;

            } else if (id == R.id.nav_reports) {
                loadFragment(new ReportsFragment());
                return true;

            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    /**
     * Tự động login lại ngầm khi app khởi động để lấy cookie session mới.
     * Cookie từ lần login trước đã hết hạn sau khi app bị kill.
     */
    private void autoLoginIfNeeded() {
        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) return;

        String loginName = session.getLoginName();
        String password  = session.getPassword();

        if (loginName.isEmpty() || password.isEmpty()) {
            Log.w("AUTO_LOGIN", "Thiếu credentials, yêu cầu login lại");
            redirectToLogin(session);
            return;
        }

        Log.d("AUTO_LOGIN", "Đang auto-login cho: " + loginName);

        // Reset service để đảm bảo dùng client mới, cookie sạch
        RetrofitClient.resetService();

        LoginRequest request = new LoginRequest(loginName, password);

        RetrofitClient.getService()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isLoggedIn()) {

                            // ✅ Cookie đã được lưu tự động qua cookieJar sau login
                            // Cập nhật token mới vào session
                            session.saveToken(response.body().getUserTokenKey());
                            Log.d("AUTO_LOGIN", "Auto-login thành công ✅");

                        } else {
                            Log.w("AUTO_LOGIN", "Auto-login thất bại, redirect về login");
                            redirectToLogin(session);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // Mất mạng tạm thời — không logout, để user thử lại
                        Log.e("AUTO_LOGIN", "Lỗi mạng khi auto-login: " + t.getMessage());
                    }
                });
    }

    private void redirectToLogin(SessionManager session) {
        session.logout();
        RetrofitClient.resetService();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}