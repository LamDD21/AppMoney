package com.example.appmoney.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.appmoney.LoginActivity;
import com.example.appmoney.databinding.FragmentProfileBinding;
import com.example.appmoney.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        session = new SessionManager(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String fullName = session.getFullName();
        String email = session.getEmail();
        String loginName = session.getLoginName();

        binding.tvProfileName.setText(fullName);
        binding.tvProfileEmail.setText(email.isEmpty() ? loginName : email);

        if (fullName != null && !fullName.isEmpty()) {
            binding.tvAvatar.setText(
                    String.valueOf(fullName.charAt(0)).toUpperCase()
            );
        }

        binding.itemName.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Tên: " + fullName, Toast.LENGTH_SHORT).show());

        binding.itemNotifications.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Cài đặt thông báo", Toast.LENGTH_SHORT).show());

        binding.itemSecurity.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Bảo mật", Toast.LENGTH_SHORT).show());

        binding.btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất?")
                        .setNegativeButton("Huỷ", null)
                        .setPositiveButton("Đăng xuất", (d, w) -> {
                            session.logout();

                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}