package com.example.appmoney.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appmoney.R;
import com.example.appmoney.database.AppDatabase;
import com.example.appmoney.databinding.FragmentBudgetBinding;
import com.example.appmoney.databinding.ItemBudgetBinding;

import java.util.concurrent.Executors;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;

    private static final double LIMIT_FOOD = 10_000_000;
    private static final double LIMIT_TRANSPORT = 5_000_000;
    private static final double LIMIT_ENTERTAINMENT = 3_000_000;
    private static final double LIMIT_HOUSING = 10_000_000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAddBudget.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Tính năng sắp ra mắt!", Toast.LENGTH_SHORT).show()
        );

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

            double food = getAbs(db, "food");
            double trans = getAbs(db, "transport");
            double entert = getAbs(db, "entertainment");
            double housing = getAbs(db, "housing");

            double total = LIMIT_FOOD + LIMIT_TRANSPORT
                    + LIMIT_ENTERTAINMENT + LIMIT_HOUSING;

            if (getActivity() == null || binding == null) return;

            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;

                binding.tvTotalBudget.setText(String.format("%,.0fđ", total));

                fill(binding.budgetFood,
                        "🍔", "Ăn uống", food, LIMIT_FOOD);

                fill(binding.budgetTransport,
                        "🚕", "Di chuyển", trans, LIMIT_TRANSPORT);

                fill(binding.budgetEntertainment,
                        "🎬", "Giải trí", entert, LIMIT_ENTERTAINMENT);

                fill(binding.budgetHousing,
                        "🏠", "Nhà cửa", housing, LIMIT_HOUSING);
            });
        });
    }

    private double getAbs(AppDatabase db, String cat) {
        Double v = db.transactionDao().getSpentByCategory(cat);
        return (v != null) ? Math.abs(v) : 0.0;
    }

    private void fill(ItemBudgetBinding item,
                      String icon,
                      String name,
                      double spent,
                      double limit) {

        item.tvCatIcon.setText(icon);
        item.tvCatName.setText(name);
        item.tvSpent.setText(String.format("%,.0fđ", spent));
        item.tvBudgetLimit.setText(String.format("/ %,.0fđ", limit));

        int pct = 0;
        if (limit > 0) {
            pct = (int) Math.min((spent / limit) * 100, 100);
        }

        item.progressBudget.setProgress(pct);

        if (pct >= 90) {
            item.tvMood.setText("😰");
            item.tvSpent.setTextColor(requireContext().getColor(R.color.danger));
        } else if (pct >= 70) {
            item.tvMood.setText("😐");
            item.tvSpent.setTextColor(requireContext().getColor(R.color.warning));
        } else {
            item.tvMood.setText("😊");
            item.tvSpent.setTextColor(requireContext().getColor(R.color.success));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}