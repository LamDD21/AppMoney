package com.example.appmoney.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String type;         // "expense" hoặc "income"
    public String category;     // "food", "transport"...
    public double amount;       // âm = chi tiêu, dương = thu nhập
    public String description;
    public long date;

    public Transaction(String type, String category,
                       double amount, String description, long date) {
        this.type        = type;
        this.category    = category;
        this.amount      = amount;
        this.description = description;
        this.date        = date;
    }

    public String getCategoryEmoji() {
        switch (category) {
            case "food":          return "🍔";
            case "transport":     return "🚕";
            case "entertainment": return "🎬";
            case "health":        return "💊";
            case "housing":       return "🏠";
            case "salary":        return "💼";
            case "bonus":         return "💰";
            default:              return "📦";
        }
    }

    public String getCategoryName() {
        switch (category) {
            case "food":          return "Ăn uống";
            case "transport":     return "Di chuyển";
            case "entertainment": return "Giải trí";
            case "health":        return "Sức khoẻ";
            case "housing":       return "Nhà cửa";
            case "salary":        return "Lương";
            case "bonus":         return "Thưởng";
            default:              return "Khác";
        }
    }

    public String getFormattedAmount() {
        String prefix = isExpense() ? "-" : "+";
        return prefix + String.format("%,.0fđ", Math.abs(amount));
    }

    public boolean isExpense() {
        return "expense".equals(type);
    }
}