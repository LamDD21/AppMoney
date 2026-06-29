package com.example.appmoney;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appmoney.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        TextView tv = new TextView(requireContext());
        tv.setText("👤 Tài khoản");
        tv.setTextColor(getResources().getColor(R.color.text_primary, null));
        tv.setTextSize(24f);
        tv.setGravity(Gravity.CENTER);

        Button btnLogout = new Button(requireContext());
        btnLogout.setText("Đăng xuất");

        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất?")
                        .setNegativeButton("Huỷ", null)
                        .setPositiveButton("Đăng xuất", (d, w) -> {

                            new SessionManager(requireContext()).logout();

                            Intent intent = new Intent(requireContext(),
                                    LoginActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                        }).show());

        layout.addView(tv);
        layout.addView(btnLogout);

        return layout;
    }
}