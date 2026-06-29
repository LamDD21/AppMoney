package com.example.appmoney.app.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    // Sửa tên này cho khớp với field API yêu cầu
    // Xem lại tab Body trong Postman của bạn
    @SerializedName("UserName")   // hoặc "username" tùy API
    private String userName;

    @SerializedName("Password")   // hoặc "password" tùy API
    private String password;


    @SerializedName("Captcha")
    private String captcha;

    @SerializedName("Remember")
    private boolean remember;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.captcha  = "";       // API yêu cầu nhưng để trống
        this.remember = true;
    }
}