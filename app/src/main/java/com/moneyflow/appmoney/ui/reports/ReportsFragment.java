package com.example.appmoney.ui.reports;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appmoney.database.AppDatabase;
import com.example.appmoney.databinding.FragmentReportsBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
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
    public void onResume() {
        super.onResume();
        loadReportData();
    }

    private void loadReportData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            double income = AppDatabase.getInstance(requireContext())
                    .transactionDao()
                    .getTotalIncome();

            double expense = AppDatabase.getInstance(requireContext())
                    .transactionDao()
                    .getTotalExpense();

            double saving = income - expense;
            int savingRate = income > 0 ? (int) ((saving / income) * 100) : 0;

            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;

                binding.tvReportIncome.setText(fmt(income));
                binding.tvReportExpense.setText(fmt(expense));
                binding.tvSavingRate.setText(savingRate + "%");
                binding.tvSavingDetail.setText(fmt(saving) + " / " + fmt(income));

                binding.tvTopFood.setText("0đ");
                binding.tvTopHousing.setText("0đ");
                binding.tvTopEntertainment.setText("0đ");

                setupBarChart(income, expense, saving);
                setupPieChart();
            });
        });
    }

    private void setupBarChart(double income, double expense, double saving) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) income));
        entries.add(new BarEntry(1, (float) expense));
        entries.add(new BarEntry(2, (float) saving));

        BarDataSet dataSet = new BarDataSet(entries, "Báo cáo tài chính");
        dataSet.setColors(
                Color.rgb(46, 204, 113),
                Color.rgb(231, 76, 60),
                Color.rgb(26, 188, 156)
        );
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(11f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.45f);

        binding.barIncomeExpense.setData(barData);
        binding.barIncomeExpense.getDescription().setEnabled(false);
        binding.barIncomeExpense.getLegend().setEnabled(true);
        binding.barIncomeExpense.getAxisRight().setEnabled(false);
        binding.barIncomeExpense.getXAxis().setEnabled(false);
        binding.barIncomeExpense.animateY(800);
        binding.barIncomeExpense.invalidate();
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Ăn uống"));
        entries.add(new PieEntry(35f, "Nhà cửa"));
        entries.add(new PieEntry(25f, "Giải trí"));

        PieDataSet dataSet = new PieDataSet(entries, "Top chi tiêu");
        dataSet.setColors(
                Color.rgb(255, 193, 7),
                Color.rgb(33, 150, 243),
                Color.rgb(156, 39, 176)
        );
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);

        binding.pieExpenseCategory.setData(pieData);
        binding.pieExpenseCategory.getDescription().setEnabled(false);
        binding.pieExpenseCategory.setUsePercentValues(true);
        binding.pieExpenseCategory.setCenterText("Chi tiêu");
        binding.pieExpenseCategory.setCenterTextColor(Color.WHITE);
        binding.pieExpenseCategory.setCenterTextSize(16f);
        binding.pieExpenseCategory.animateY(800);
        binding.pieExpenseCategory.invalidate();
    }

    private String fmt(double v) {
        return String.format("%,.0fđ", v);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}