package com.example.appmoney.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmoney.R;
import com.example.appmoney.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private List<Transaction> list = new ArrayList<>();
    private final Context context;
    private final OnItemClickListener listener;

    public TransactionAdapter(Context context, OnItemClickListener listener) {
        this.context  = context;
        this.listener = listener;
    }

    public void setData(List<Transaction> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Transaction t = list.get(position);

        h.tvIcon.setText(t.getCategoryEmoji());

        String name = (t.description != null && !t.description.isEmpty())
                ? t.description : t.getCategoryName();
        h.tvName.setText(name);
        h.tvDate.setText(formatDate(t.date));
        h.tvAmount.setText(t.getFormattedAmount());

        h.tvAmount.setTextColor(ContextCompat.getColor(context,
                t.isExpense() ? R.color.danger : R.color.success));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(t);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    private String formatDate(long ts) {
        return new SimpleDateFormat("dd/MM/yyyy  HH:mm",
                new Locale("vi", "VN")).format(new Date(ts));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvDate, tvAmount;
        ViewHolder(@NonNull View v) {
            super(v);
            tvIcon   = v.findViewById(R.id.tv_txn_icon);
            tvName   = v.findViewById(R.id.tv_txn_name);
            tvDate   = v.findViewById(R.id.tv_txn_date);
            tvAmount = v.findViewById(R.id.tv_txn_amount);
        }
    }
}