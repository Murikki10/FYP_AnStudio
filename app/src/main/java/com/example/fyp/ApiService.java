package com.example.fyp;


import com.google.gson.JsonObject;

import java.util.List;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // ========= 用戶相關 API =========
    @POST("api/auth/register")
    Call<ResponseBody> registerUser(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @PUT("api/users/profile")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String token,
            @Body UserProfileUpdateRequest request
    );
    @GET("user/training-records")
    Call<List<String>> getTrainingRecords(@Header("Authorization") String token);

    @GET("api/user/posts")
    Call<UserPostsResponse> getUserPosts(@Header("Authorization") String token);
    @POST("api/user/update-password")
    Call<ResponseBody> updatePassword(
            @Header("Authorization") String token,
            @Body UpdatePasswordRequest request
    );
    @GET("/api/auth/check")
    Call<ResponseBody> checkFieldAvailability(
            @Query("field") String field,
            @Query("value") String value
    );
    @GET("api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @GET("/api/user/posts/count")
    Call<PostCountResponse> getPostCount(@Header("Authorization") String token);

    // ========= 帖子相關 API =========
    @GET("/api/posts")
    Call<ApiResponse> getPosts(
            @Query("boardId") @Nullable Integer boardId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    //Board Follow
    @POST("/api/boards/{id}/follow")
    Call<Void> updateFollowStatus(@Path("id") int boardId, @Body FollowRequest followRequest);

    // 獲取單個帖子詳情
    @GET("/api/posts/{postId}")
    Call<Post> getPostDetails(
            @Header("Authorization") String token,
            @Path("postId") int postId
    );

    // 提交留言
    @POST("/api/posts/{postId}/comments")
    Call<Void> addComment(
            @Header("Authorization") String token,
            @Path("postId") int postId,
            @Body CommentRequest commentRequest
    );

    // 獲取留言列表
    @GET("/api/posts/{postId}/comments")
    Call<List<Comment>> getComments(
            @Header("Authorization") String token,
            @Path("postId") int postId
    );

    // 獲取分區列表
    @GET("/api/boards")
    Call<List<Board>> getBoards();

    // 創建帖子
    @POST("/api/posts")
    Call<PostsResponse> createPost(
            @Header("Authorization") String authToken, // 傳遞 Authorization 標頭
            @Body PostRequest postRequest              // 傳遞 PostRequest 物件
    );

    @GET("/api/tags")
    Call<List<Tag>> getTags();

    // 檢查用戶Liked?
    @GET("/api/posts/{postId}/isLiked")
    Call<JsonObject> isLiked(
            @Header("Authorization") String token,
            @Path("postId") int postId
    );
    @PUT("/api/posts/{postId}")
    Call<Post> updatePost(@Path("postId") int postId, @Body Post post);

    @DELETE("/api/posts/{postId}")
    Call<Void> deletePost(@Path("postId") int postId);

    @POST("/api/posts/{postId}/toggle-like")
    Call<Void> toggleLike(
            @Header("Authorization") String token,
            @Path("postId") int postId
    );

    @Multipart
    @POST("/upload")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part image);

    @POST("/api/posts/{postId}/toggle-follow")
    Call<Void> toggleFollow(@Path("postId") int postId);

    @GET("/api/posts/search")
    Call<List<Post>> searchPosts(
            @Query("q") String query,
            @Query("page") int page,
            @Query("limit") int limit
    );

    // ========= 計劃 & 視頻相關 API =========
    @POST("api/createPlan")
    Call<CreatePlanResponse> createPlan(
            @Header("Authorization") String token,
            @Body AddPlanRequest request
    );

    @POST("api/assignPlan")
    Call<ResponseBody> assignPlan(
            @Header("Authorization") String token,
            @Body AssignPlanRequest request
    );

    @POST("api/user/plans")
    Call<List<PlanResponse>> getUserPlans(@Body UserIdRequest request);

    @POST("api/plan/videos")
    Call<List<VideoResponse>> getPlanVideos(@Body PlanIdRequest planIdRequest);

    @GET("api/videos")
    Call<List<Video>> getVideosByType(@Query("type") String type);

    @GET("api/videos")
    Call<List<Video>> getVideos();

    @POST("api/videos/like")
    Call<ResponseBody> likeVideo(@Body LikeVideoRequest likeVideoRequest);

    @POST("api/videos/unlike")
    Call<ResponseBody> unlikeVideo(@Body LikeVideoRequest likeVideoRequest);

    @GET("/api/videos/liked")
    Call<List<Video>> getLikedVideos();
    
    // 獲取用戶計劃列表
    @POST("/api/user/plans")
    Call<List<PlanResponse>> getUserPlansWithToken(@Header("Authorization") String authToken);

    // 獲取計劃視頻列表
    @POST("/api/plan/videos")
    Call<List<VideoResponse>> getPlanVideosWithToken(
            @Header("Authorization") String authToken,
            @Body PlanIdRequest planIdRequest
    );

    // ========= 活動 (Event) 相關 API =========

    // 獲取所有活動列表
    @GET("/api/events")
    Call<List<Event>> getAllEvents();

    // 獲取單個活動詳情
    @GET("/api/events/{eventId}")
    Call<Event> getEventDetails(@Path("eventId") int eventId);

    // 報名活動
    @POST("/api/events/{eventId}/register")
    Call<ResponseBody> registerEvent(@Path("eventId") int eventId, @Body RequestBody requestBody);


    //獲取用戶註冊的活動列表
    @GET("api/users/registered-events")
    Call<List<Event>> getRegisteredEvents(@Header("Authorization") String token);

    // 用戶 Check-in
    @POST("/api/events/{eventId}/check-in")
    Call<ResponseBody> checkInEvent(
            @Header("Authorization") String token,
            @Path("eventId") int eventId,
            @Body CheckInRequest checkInRequest
    );

    // 獲取活動的表單欄位
    @GET("/api/events/{eventId}/form-fields")
    Call<List<FormField>> getEventFormFields(
            @Path("eventId") int eventId
    );

    // 創建活動
    @POST("/api/events")
    Call<ResponseBody> createEvent(
            @Header("Authorization") String token,
            @Body EventRequest eventRequest
    );



}