package com.example.appmoney.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionListResponse {

    @SerializedName("Data")
    private List<ApiTransactionItem> data;

    @SerializedName("Success")
    private boolean success;

    public List<ApiTransactionItem> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}