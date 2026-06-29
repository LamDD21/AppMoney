package com.example.appmoney.ui.transaction;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appmoney.R;
import com.example.appmoney.database.AppDatabase;
import com.example.appmoney.databinding.ActivityTransactionDetailBinding;
import com.example.appmoney.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TransactionDetailActivity extends AppCompatActivity {

    private ActivityTransactionDetailBinding binding;
    private Transaction currentTxn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailBinding.inflate(
                getLayoutInflater());
        setContentView(binding.getRoot());

        long id = getIntent().getLongExtra("transaction_id", -1);

        if (id == -1) {
            Toast.makeText(this, "Không tìm thấy giao dịch",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load dữ liệu từ DB
        Executors.newSingleThreadExecutor().execute(() -> {
            currentTxn = AppDatabase.getInstance(getApplicationContext())
                    .transactionDao().getById(id);
            runOnUiThread(() -> {
                if (currentTxn != null) {
                    bindData(currentTxn);
                } else {
                    Toast.makeText(this, "Không tìm thấy giao dịch",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void bindData(Transaction t) {
        binding.tvDetailIcon.setText(t.getCategoryEmoji());
        binding.tvDetailAmount.setText(t.getFormattedAmount());
        binding.tvDetailCategory.setText(t.getCategoryName());
        binding.tvDetailDesc.setText(t.description);
        binding.tvDetailType.setText(
                t.isExpense() ? "Chi tiêu" : "Thu nhập");
        binding.tvDetailType.setTextColor(getColor(
                t.isExpense() ? R.color.danger : R.color.success));

        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEEE, dd/MM/yyyy  HH:mm",
                new Locale("vi", "VN"));
        binding.tvDetailDate.setText(sdf.format(new Date(t.date)));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá giao dịch")
                .setMessage("Bạn có chắc muốn xoá giao dịch này?")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (d, w) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getInstance(getApplicationContext())
                                .transactionDao().delete(currentTxn);
                        runOnUiThread(() -> {
                            Toast.makeText(this,
                                    "Đã xoá!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                }).show();
    }
}