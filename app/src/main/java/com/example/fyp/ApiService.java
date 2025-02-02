package com.example.fyp;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @POST("api/auth/register")  // 正確的註冊路徑
    Call<ResponseBody> registerUser(@Body RegisterRequest request);

    @POST("api/auth/login")  // 正確的登入路徑
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @PUT("api/users/profile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    @POST("api/user/update-password")
    Call<ResponseBody> updatePassword(
            @Header("Authorization") String token,
            @Body UpdatePasswordRequest request
    );

    // 獲取用戶個人資料的 API
    @GET("api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);
}