package com.example.appmoney;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.moneyflow.app.databinding.ActivityMainBinding;
import com.moneyflow.app.ui.home.HomeFragment;
import com.moneyflow.app.ui.profile.ProfileFragment;
import com.moneyflow.app.ui.reports.ReportsFragment;
import com.moneyflow.app.ui.transaction.AddTransactionActivity;
import com.moneyflow.app.ui.transaction.TransactionsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                // Mở màn thêm giao dịch
                startActivity(new Intent(this,
                        AddTransactionActivity.class));
                return false; // Không đổi tab

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

    // Hàm dùng chung để chuyển Fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}