package com.example.appmoney.app.api;

import com.example.appmoney.app.model.LoginRequest;
import com.example.appmoney.app.model.LoginResponse;
import com.example.appmoney.app.model.TransactionListResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("_vt_api/Authentication/ValidateUserRemote")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ✅ Không cần Authorization header — cookie tự động gửi qua cookieJar
    @POST("_vt_api/List/GetAllItems")
    Call<TransactionListResponse> getAllTransactions(
            @Body Map<String, Object> body
    );
}