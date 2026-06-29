package com.example.appmoney.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME      = "appmoney_prefs";
    private static final String KEY_LOGGED_IN  = "is_logged_in";
    private static final String KEY_TOKEN      = "user_token_key";
    private static final String KEY_LOGIN_NAME = "login_name";
    private static final String KEY_FULL_NAME  = "full_name";
    private static final String KEY_EMAIL      = "email";
    private static final String KEY_USER_ID    = "user_id";
    private static final String KEY_IS_ADMIN   = "is_admin";
    private static final String KEY_PASSWORD   = "password"; // ✅ thêm mới

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ✅ Lưu toàn bộ thông tin sau khi login thành công (có thêm password)
    public void saveLoginData(String tokenKey, String loginName,
                              String fullName,  String email,
                              int userId,       boolean isAdmin,
                              String password) {
        editor.putBoolean(KEY_LOGGED_IN,  true);
        editor.putString(KEY_TOKEN,       tokenKey);
        editor.putString(KEY_LOGIN_NAME,  loginName);
        editor.putString(KEY_FULL_NAME,   fullName);
        editor.putString(KEY_EMAIL,       email);
        editor.putInt(KEY_USER_ID,        userId);
        editor.putBoolean(KEY_IS_ADMIN,   isAdmin);
        editor.putString(KEY_PASSWORD,    password); // ✅
        editor.apply();
    }

    // Cập nhật token mới sau auto-login
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token).apply();
    }

    // ── GETTERS ──────────────────────────────────────────

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public String getLoginName() {
        return prefs.getString(KEY_LOGIN_NAME, "");
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "Người dùng");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, 0);
    }

    public boolean isAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, "");
    }

    // Xoá session khi đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}