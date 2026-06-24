package com.moneyflow.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.moneyflow.app.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemName.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Chỉnh sửa tên", Toast.LENGTH_SHORT).show());

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
                        .setPositiveButton("Đăng xuất", (d, w) ->
                                requireActivity().finish())
                        .show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}