package com.moneyflow.app.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moneyflow.app.R;
import com.moneyflow.app.database.AppDatabase;
import com.moneyflow.app.databinding.ActivityAddTransactionBinding;
import com.moneyflow.app.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private String selectedType     = "expense";
    private String selectedCategory = "food";
    private final Calendar calendar = Calendar.getInstance();
    private List<LinearLayout> catViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận type từ Intent (expense / income)
        String intentType = getIntent().getStringExtra("type");
        if (intentType != null) selectedType = intentType;

        updateTypeUI();
        updateDateText();
        setupCategories();

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Toggle Chi tiêu / Thu nhập
        binding.btnExpense.setOnClickListener(v -> {
            selectedType = "expense";
            updateTypeUI();
        });
        binding.btnIncome.setOnClickListener(v -> {
            selectedType = "income";
            updateTypeUI();
        });

        // Chọn ngày
        binding.layoutDate.setOnClickListener(v -> showDatePicker());

        // Lưu giao dịch
        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    // Cập nhật giao diện nút Chi tiêu / Thu nhập
    private void updateTypeUI() {
        if ("expense".equals(selectedType)) {
            binding.tvTitle.setText("Thêm Chi Tiêu");
            binding.btnExpense.setBackgroundResource(R.drawable.bg_btn_primary);
            binding.btnExpense.setTextColor(getColor(R.color.text_primary));
            binding.btnIncome.setBackgroundColor(
                    android.graphics.Color.TRANSPARENT);
            binding.btnIncome.setTextColor(getColor(R.color.text_secondary));
        } else {
            binding.tvTitle.setText("Thêm Thu Nhập");
            binding.btnIncome.setBackgroundResource(R.drawable.bg_btn_primary);
            binding.btnIncome.setTextColor(getColor(R.color.text_primary));
            binding.btnExpense.setBackgroundColor(
                    android.graphics.Color.TRANSPARENT);
            binding.btnExpense.setTextColor(getColor(R.color.text_secondary));
        }
    }

    // Gắn sự kiện click cho 6 ô danh mục
    private void setupCategories() {
        catViews = new ArrayList<>();
        catViews.add(binding.catFood);
        catViews.add(binding.catTransport);
        catViews.add(binding.catEntertainment);
        catViews.add(binding.catHealth);
        catViews.add(binding.catHousing);
        catViews.add(binding.catOther);

        String[] keys = {
                "food","transport","entertainment",
                "health","housing","other"
        };

        for (int i = 0; i < catViews.size(); i++) {
            final String key = keys[i];
            catViews.get(i).setOnClickListener(v -> {
                // Bỏ chọn tất cả
                for (LinearLayout lv : catViews) lv.setSelected(false);
                // Chọn cái vừa bấm
                v.setSelected(true);
                selectedCategory = key;
            });
        }

        // Mặc định chọn "Ăn uống"
        binding.catFood.setSelected(true);
    }

    // Mở DatePicker để chọn ngày
    private void showDatePicker() {
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    updateDateText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd/MM/yyyy", new Locale("vi", "VN"));
        binding.tvDate.setText(sdf.format(calendar.getTime()));
    }

    // Lưu vào database
    private void saveTransaction() {
        String amountStr = binding.etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this,
                    "Số tiền phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        String desc = binding.etDescription.getText().toString().trim();
        if (desc.isEmpty()) desc = getCategoryName(selectedCategory);

        // Chi tiêu → số âm
        if ("expense".equals(selectedType)) amount = -amount;

        Transaction txn = new Transaction(
                selectedType,
                selectedCategory,
                amount,
                desc,
                calendar.getTimeInMillis()
        );

        // Lưu vào DB trên background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(getApplicationContext())
                    .transactionDao().insert(txn);
            runOnUiThread(() -> {
                Toast.makeText(this,
                        "✅ Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại màn trước
            });
        });
    }

    private String getCategoryName(String key) {
        switch (key) {
            case "food":          return "Ăn uống";
            case "transport":     return "Di chuyển";
            case "entertainment": return "Giải trí";
            case "health":        return "Sức khoẻ";
            case "housing":       return "Nhà cửa";
            default:              return "Khác";
        }
    }
}