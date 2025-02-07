package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static Context appContext;

    // 初始化 AuthManager（在 Application 中調用）
    public static void init(Context context) {
        appContext = context.getApplicationContext(); // 使用 Application Context
    }

    // 保存令牌及用戶資訊
    public static void saveAuthToken(String token, String email) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token); // 保存令牌
        editor.putString(KEY_USER_EMAIL, email); // 保存 Email
        editor.putBoolean(KEY_IS_LOGGED_IN, true); // 設定登入狀態
        editor.apply();
    }

    // 獲取令牌
    public static String getAuthToken() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // 獲取用戶 Email
    public static String getUserEmail() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    // 檢查是否已登入
    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 清除登入狀態
    public static void logout() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 清空所有數據
        editor.apply();
    }
}