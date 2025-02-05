package com.example.fyp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    @POST("api/auth/register")
    Call<ResponseBody> registerUser(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @PUT("api/users/profile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String token,
            @Body UserProfileUpdateRequest request
    );

    @POST("api/user/update-password")
    Call<ResponseBody> updatePassword(
            @Header("Authorization") String token,
            @Body UpdatePasswordRequest request
    );

    @GET("api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);
}