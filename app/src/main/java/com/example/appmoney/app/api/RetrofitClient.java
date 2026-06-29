package com.example.appmoney.app.api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://qltc.esdglobal.com.vn/";
    private static Retrofit retrofit;
    private static ApiService service;

    // ✅ Public để MainActivity có thể inject cookie vào
    private static final CookieManager cookieManager = new CookieManager(
            null, CookiePolicy.ACCEPT_ALL
    );

    public static CookieManager getCookieManager() {
        return cookieManager;
    }

    public static ApiService getService() {
        if (service == null) {
            service = buildService();
        }
        return service;
    }

    // Gọi sau khi login thành công để reset client
    public static void resetService() {
        service = null;
        retrofit = null;
        cookieManager.getCookieStore().removeAll();
    }

    private static ApiService buildService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(false)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}