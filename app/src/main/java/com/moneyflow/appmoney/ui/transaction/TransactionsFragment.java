package com.example.appmoney.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appmoney.R;
import com.example.appmoney.adapter.TransactionAdapter;
import com.example.appmoney.app.api.RetrofitClient;
import com.example.appmoney.app.model.ApiTransactionItem;
import com.example.appmoney.app.model.TransactionListResponse;
import com.example.appmoney.database.AppDatabase;
import com.example.appmoney.databinding.FragmentTransactionsBinding;
import com.example.appmoney.model.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private TransactionAdapter adapter;
    private boolean hasSynced = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        setupTabs();
        if (!hasSynced) {
            hasSynced = true;
            syncTransactionsFromApi();
        }
    }

    private void setupAdapter() {
        adapter = new TransactionAdapter(requireContext(), txn -> {
            Intent i = new Intent(requireContext(), TransactionDetailActivity.class);
            i.putExtra("transaction_id", txn.id);
            startActivity(i);
        });
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);
        setTab(0);
        loadAll();
    }

    private void setupTabs() {
        binding.tabAll.setOnClickListener(v -> { setTab(0); loadAll(); });
        binding.tabExpense.setOnClickListener(v -> { setTab(1); loadType("expense"); });
        binding.tabIncome.setOnClickListener(v -> { setTab(2); loadType("income"); });
    }

    private void setTab(int i) {
        if (binding == null) return;
        int on  = requireContext().getColor(R.color.primary);
        int off = requireContext().getColor(R.color.text_secondary);
        binding.tabAll.setTextColor(i == 0 ? on : off);
        binding.tabExpense.setTextColor(i == 1 ? on : off);
        binding.tabIncome.setTextColor(i == 2 ? on : off);
        binding.tabAll.setBackgroundResource(i == 0 ? R.drawable.bg_btn_primary : android.R.color.transparent);
        binding.tabExpense.setBackgroundResource(i == 1 ? R.drawable.bg_btn_primary : android.R.color.transparent);
        binding.tabIncome.setBackgroundResource(i == 2 ? R.drawable.bg_btn_primary : android.R.color.transparent);
    }

    private void loadAll() {
        AppDatabase.getInstance(requireContext()).transactionDao().getAll()
                .observe(getViewLifecycleOwner(), adapter::setData);
    }

    private void loadType(String type) {
        AppDatabase.getInstance(requireContext()).transactionDao().getByType(type)
                .observe(getViewLifecycleOwner(), adapter::setData);
    }

    private void syncTransactionsFromApi() {
        Log.d("SYNC", "Bắt đầu sync từ API...");

        Map<String, Object> spQuery = new HashMap<>();
        spQuery.put("OrderBy", "");
        spQuery.put("AllowPaging", true);
        spQuery.put("RowLimit", 10);
        spQuery.put("Paging", 1);
        spQuery.put("ViewFields", "");

        Map<String, Object> body = new HashMap<>();
        body.put("List", "GiaoDich");
        body.put("SPQuery", spQuery);
        body.put("Web", null);

        // Cookie tự động gửi qua cookieJar, không cần truyền token thủ công
        RetrofitClient.getService()
                .getAllTransactions(body)
                .enqueue(new Callback<TransactionListResponse>() {
                    @Override
                    public void onResponse(Call<TransactionListResponse> call,
                                           Response<TransactionListResponse> response) {
                        if (binding == null) return;

                        if (!response.isSuccessful()) {
                            try {
                                String errBody = response.errorBody() != null
                                        ? response.errorBody().string() : "null";
                                Log.e("API_ERROR", "Code: " + response.code() + " | " + errBody);
                            } catch (Exception e) {
                                Log.e("API_ERROR", "Code: " + response.code());
                            }
                            Toast.makeText(requireContext(),
                                    "Server lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TransactionListResponse result = response.body();
                        if (result == null) {
                            Log.e("API_ERROR", "Response body null");
                            Toast.makeText(requireContext(),
                                    "Server trả về sai định dạng.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!result.isSuccess()) {
                            Log.e("API_ERROR", "isSuccess = false");
                            Toast.makeText(requireContext(),
                                    "API không thành công", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (result.getData() == null || result.getData().isEmpty()) {
                            Log.w("API_WARN", "Data rỗng");
                            Toast.makeText(requireContext(),
                                    "Không có dữ liệu giao dịch", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("API_OK", "Nhận được " + result.getData().size() + " giao dịch");
                        saveApiDataToRoom(result);
                    }

                    @Override
                    public void onFailure(Call<TransactionListResponse> call, Throwable t) {
                        if (binding == null) return;
                        Log.e("API_FAIL", "onFailure: " + t.getMessage(), t);
                        Toast.makeText(requireContext(),
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveApiDataToRoom(TransactionListResponse result) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            for (ApiTransactionItem item : result.getData()) {
                String category = mapCategory(item.getType());
                String title = item.getTitle();
                if (title == null || title.trim().isEmpty()) title = item.getNote();
                if (title == null || title.trim().isEmpty()) title = category;
                double amount = item.getAmount();
                if (amount > 0) amount = -amount;
                Transaction txn = new Transaction("expense", category, amount, title, System.currentTimeMillis());
                db.transactionDao().insert(txn);
                Log.d("ROOM_INSERT", "Lưu: " + title + " | " + amount);
            }
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    if (binding != null) {
                        Toast.makeText(requireContext(),
                                "Đã đồng bộ " + result.getData().size() + " giao dịch",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String mapCategory(String apiType) {
        if (apiType == null) return "other";
        String v = apiType.trim().toLowerCase();
        if (v.contains("ăn")) return "food";
        if (v.contains("di chuyển") || v.contains("taxi")) return "transport";
        if (v.contains("giải trí")) return "entertainment";
        if (v.contains("sức")) return "health";
        if (v.contains("nhà")) return "housing";
        return "other";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}