package com.bangkit2024.educook.api

import com.bangkit2024.educook.data.response.LoginResponse
import com.bangkit2024.educook.data.response.RegisterResponse
import com.bangkit2024.educook.data.response.MenuResponse
import com.bangkit2024.educook.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    fun fetchStories(
        @Header("Authorization") authToken: String,
    ): Call<MenuResponse>

    @Multipart
    @POST("stories")
    suspend fun addRecipe(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse
}