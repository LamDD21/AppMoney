package com.moneyflow.app.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.moneyflow.app.database.AppDatabase;
import com.moneyflow.app.databinding.FragmentReportsBinding;

import java.util.concurrent.Executors;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            double income  = db.transactionDao().getTotalIncome();
            double expense = db.transactionDao().getTotalExpense();
            double saving  = income - expense;
            int    rate    = (income > 0)
                    ? (int) ((saving / income) * 100) : 0;

            double food    = getAbs(db, "food");
            double housing = getAbs(db, "housing");
            double entert  = getAbs(db, "entertainment");

            requireActivity().runOnUiThread(() -> {
                binding.tvSavingRate.setText(rate + "%");
                binding.tvSavingDetail.setText(
                        String.format("%,.0fđ  /  %,.0fđ",
                                saving, income));
                binding.tvReportIncome.setText(
                        String.format("%,.0fđ", income));
                binding.tvReportExpense.setText(
                        String.format("%,.0fđ", expense));
                binding.tvTopFood.setText(
                        String.format("%,.0fđ", food));
                binding.tvTopHousing.setText(
                        String.format("%,.0fđ", housing));
                binding.tvTopEntertainment.setText(
                        String.format("%,.0fđ", entert));
            });
        });
    }

    private double getAbs(AppDatabase db, String cat) {
        Double v = db.transactionDao().getSpentByCategory(cat);
        return (v != null) ? Math.abs(v) : 0.0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}