package com.moneyflow.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.moneyflow.app.MainActivity;
import com.moneyflow.app.adapter.TransactionAdapter;
import com.moneyflow.app.database.AppDatabase;
import com.moneyflow.app.databinding.FragmentHomeBinding;
import com.moneyflow.app.ui.budget.BudgetFragment;
import com.moneyflow.app.ui.reports.ReportsFragment;
import com.moneyflow.app.ui.transaction.AddTransactionActivity;
import com.moneyflow.app.ui.transaction.TransactionDetailActivity;
import com.moneyflow.app.ui.transaction.TransactionsFragment;

import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSummary();
    }

    private void setupRecyclerView() {
        TransactionAdapter adapter = new TransactionAdapter(
                requireContext(),
                txn -> {
                    Intent i = new Intent(requireContext(),
                            TransactionDetailActivity.class);
                    i.putExtra("transaction_id", txn.id);
                    startActivity(i);
                });

        binding.rvRecent.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvRecent.setAdapter(adapter);
        binding.rvRecent.setNestedScrollingEnabled(false);

        AppDatabase.getInstance(requireContext())
                .transactionDao().getAll()
                .observe(getViewLifecycleOwner(), list -> {
                    adapter.setData(list.size() > 5
                            ? list.subList(0, 5) : list);
                });
    }

    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {
            double income  = AppDatabase.getInstance(requireContext())
                    .transactionDao().getTotalIncome();
            double expense = AppDatabase.getInstance(requireContext())
                    .transactionDao().getTotalExpense();
            double balance = income - expense;

            requireActivity().runOnUiThread(() -> {
                binding.tvBalance.setText(fmt(balance));
                binding.tvIncome.setText(fmt(income));
                binding.tvExpense.setText(fmt(expense));
            });
        });
    }

    private void setupClickListeners() {
        binding.btnAddIncome.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(),
                    AddTransactionActivity.class);
            i.putExtra("type", "income");
            startActivity(i);
        });

        binding.btnAddExpense.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(),
                    AddTransactionActivity.class);
            i.putExtra("type", "expense");
            startActivity(i);
        });

        binding.menuTransactions.setOnClickListener(v ->
                go(new TransactionsFragment()));
        binding.menuBudget.setOnClickListener(v ->
                go(new BudgetFragment()));
        binding.menuReports.setOnClickListener(v ->
                go(new ReportsFragment()));
        binding.menuCategories.setOnClickListener(v ->
                go(new TransactionsFragment()));
        binding.tvSeeAll.setOnClickListener(v ->
                go(new TransactionsFragment()));
    }

    private void go(Fragment f) {
        ((MainActivity) requireActivity()).loadFragment(f);
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