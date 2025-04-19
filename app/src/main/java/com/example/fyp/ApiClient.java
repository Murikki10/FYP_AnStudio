package com.example.fyp;

import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://13.239.37.192:5001/"; // 本地 API 地址
    private static Retrofit retrofit = null;

    // 保留原有的 getClient 方法
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = createRetrofitInstance();
        }
        return retrofit;
    }

    // 新增 getRetrofitInstance 方法，與 getClient 功能一致
    public static Retrofit getRetrofitInstance() {
        return getClient();
    }

    // 抽取 Retrofit 初始化邏輯，避免重複代碼
    private static Retrofit createRetrofitInstance() {
        // Logging Interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Authorization Interceptor
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            // 從 AuthManager 獲取令牌
            String token = AuthManager.getAuthToken();
            if (token != null && !token.isEmpty()) {
                builder.addHeader("Authorization", "Bearer " + token);
            }

            Request modifiedRequest = builder.build();
            return chain.proceed(modifiedRequest);
        };

        // OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Retrofit
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}