package com.ahmedsamir.pulse.core.network.api

import com.ahmedsamir.pulse.core.network.dto.CommentDto
import com.ahmedsamir.pulse.core.network.dto.NotificationDto
import com.ahmedsamir.pulse.core.network.dto.PostDto
import com.ahmedsamir.pulse.core.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PulseApi {

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserDto>

    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<List<UserDto>>

    @GET("users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<UserDto>>

    @GET("users/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<UserDto>>

    @POST("users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String): Response<Unit>

    @DELETE("users/{userId}/follow")
    suspend fun unfollowUser(@Path("userId") userId: String): Response<Unit>

    @PUT("users/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Body request: Map<String, String>
    ): Response<UserDto>

    @GET("feed")
    suspend fun getFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<PostDto>>

    @GET("posts/{postId}")
    suspend fun getPost(@Path("postId") postId: String): Response<PostDto>

    @POST("posts")
    suspend fun createPost(@Body request: Map<String, String>): Response<PostDto>

    @DELETE("posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): Response<Unit>

    @POST("posts/{postId}/like")
    suspend fun likePost(@Path("postId") postId: String): Response<Unit>

    @DELETE("posts/{postId}/like")
    suspend fun unlikePost(@Path("postId") postId: String): Response<Unit>

    @POST("posts/{postId}/repost")
    suspend fun repostPost(@Path("postId") postId: String): Response<Unit>

    @GET("posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<CommentDto>>

    @POST("posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: String,
        @Body request: Map<String, String>
    ): Response<CommentDto>

    @DELETE("posts/{postId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String
    ): Response<Unit>

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<NotificationDto>>

    @PUT("notifications/{notificationId}/read")
    suspend fun markNotificationRead(
        @Path("notificationId") notificationId: String
    ): Response<Unit>
}