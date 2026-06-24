package com.moneyflow.app.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.moneyflow.app.R;
import com.moneyflow.app.adapter.TransactionAdapter;
import com.moneyflow.app.database.AppDatabase;
import com.moneyflow.app.databinding.FragmentTransactionsBinding;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        setupTabs();
    }

    private void setupAdapter() {
        adapter = new TransactionAdapter(requireContext(), txn -> {
            Intent i = new Intent(requireContext(),
                    TransactionDetailActivity.class);
            i.putExtra("transaction_id", txn.id);
            startActivity(i);
        });
        binding.rvTransactions.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);
        loadAll();
    }

    private void setupTabs() {
        binding.tabAll.setOnClickListener(v -> {
            setTab(0); loadAll();
        });
        binding.tabExpense.setOnClickListener(v -> {
            setTab(1); loadType("expense");
        });
        binding.tabIncome.setOnClickListener(v -> {
            setTab(2); loadType("income");
        });
    }

    private void setTab(int i) {
        int on  = requireContext().getColor(R.color.primary);
        int off = requireContext().getColor(R.color.text_secondary);
        binding.tabAll.setTextColor(i == 0 ? on : off);
        binding.tabExpense.setTextColor(i == 1 ? on : off);
        binding.tabIncome.setTextColor(i == 2 ? on : off);

        binding.tabAll.setBackgroundResource(
                i == 0 ? R.drawable.bg_btn_primary : android.R.color.transparent);
        binding.tabExpense.setBackgroundResource(
                i == 1 ? R.drawable.bg_btn_primary : android.R.color.transparent);
        binding.tabIncome.setBackgroundResource(
                i == 2 ? R.drawable.bg_btn_primary : android.R.color.transparent);
    }

    private void loadAll() {
        AppDatabase.getInstance(requireContext())
                .transactionDao().getAll()
                .observe(getViewLifecycleOwner(), adapter::setData);
    }

    private void loadType(String type) {
        AppDatabase.getInstance(requireContext())
                .transactionDao().getByType(type)
                .observe(getViewLifecycleOwner(), adapter::setData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}