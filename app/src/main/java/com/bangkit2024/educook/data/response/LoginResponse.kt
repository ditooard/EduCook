package com.bangkit2024.educook.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("role")
    val role: String,

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("id")
    val id: String,
)

data class Data(

    @field:SerializedName("token")
    val token: String
)

