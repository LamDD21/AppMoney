package com.example.appmoney.app.model;

import com.google.gson.annotations.SerializedName;

public class ApiTransactionItem {

    @SerializedName("ID")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("amount")
    private double amount;

    @SerializedName("type")
    private String type;

    @SerializedName("note")
    private String note;

    @SerializedName("transaction_date")
    private String transactionDate;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title != null ? title.trim() : "";
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type != null ? type.trim() : "";
    }

    public String getNote() {
        return note != null ? note.trim() : "";
    }

    public String getTransactionDate() {
        return transactionDate != null ? transactionDate.trim() : "";
    }
}